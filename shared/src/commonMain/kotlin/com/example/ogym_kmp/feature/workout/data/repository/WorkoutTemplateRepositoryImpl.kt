package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.feature.workout.data.mapper.toDomain
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.workout.domain.model.TemplateExercise
import com.example.ogym_kmp.feature.workout.domain.model.TemplateSet
import com.example.ogym_kmp.feature.workout.domain.model.CreateWorkoutTemplateInput
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutTemplateRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateTemplateExerciseRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateTemplateSetRequestDto
import kotlinx.coroutines.CancellationException
import kotlin.time.Instant
import kotlin.uuid.Uuid

class WorkoutTemplateRepositoryImpl(
    private val workoutApi: WorkoutApi,
    private val database: OGymDatabase,
) : WorkoutTemplateRepository {
    override suspend fun getWorkoutTemplates(): List<WorkoutTemplate> {
        syncPendingTemplates()
        return try {
            val remoteTemplates = workoutApi.getWorkoutTemplates().map { it.toDomain() }
            replaceCache(remoteTemplates)
            remoteTemplates
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            readCache().ifEmpty { throw exception }
        }
    }

    override suspend fun createWorkoutTemplate(
        input: CreateWorkoutTemplateInput,
        createdAt: Instant,
    ): WorkoutTemplate {
        val pending = input.toTemplate(createdAt)
        cacheTemplate(pending, "PENDING_SYNC")
        return try {
            val synced = workoutApi.createWorkoutTemplate(input.toRequest()).toDomain()
            cacheTemplate(synced, "SYNCED")
            synced
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            pending
        }
    }

    override suspend fun updateWorkoutTemplate(
        input: CreateWorkoutTemplateInput,
        updatedAt: Instant,
    ): WorkoutTemplate {
        val existing = readCache().firstOrNull { it.id == input.clientGeneratedId }
            ?: error("Template not found.")
        val pending = input.toTemplate(updatedAt).copy(
            ownerId = existing.ownerId,
            createdAt = existing.createdAt,
        )
        val currentSyncStatus = database.workoutTemplateCacheQueries.selectCachedWorkoutTemplates()
            .executeAsList()
            .firstOrNull { it.id == input.clientGeneratedId.toString() }
            ?.sync_status
        cacheTemplate(
            pending,
            if (currentSyncStatus == "PENDING_SYNC") "PENDING_SYNC" else "PENDING_UPDATE",
        )
        return try {
            val synced = workoutApi.updateWorkoutTemplate(input.toRequest()).toDomain()
            cacheTemplate(synced, "SYNCED")
            synced
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            pending
        }
    }

    override suspend fun deleteWorkoutTemplate(id: Uuid) {
        val templateId = id.toString()
        val syncStatus = database.workoutTemplateCacheQueries.selectCachedWorkoutTemplates()
            .executeAsList()
            .firstOrNull { it.id == templateId }
            ?.sync_status
        if (syncStatus != "PENDING_SYNC") {
            workoutApi.deleteWorkoutTemplate(templateId)
        }
        database.transaction {
            database.workoutTemplateCacheQueries.deleteTemplateSetsForTemplate(templateId)
            database.workoutTemplateCacheQueries.deleteTemplateExercisesForTemplate(templateId)
            database.workoutTemplateCacheQueries.deleteCachedWorkoutTemplate(templateId)
        }
    }

    private suspend fun syncPendingTemplates() {
        val pending = database.workoutTemplateCacheQueries.selectPendingWorkoutTemplates()
            .executeAsList().associate { Uuid.parse(it.id) to it.sync_status }
        readCache().filter { it.id in pending }.forEach { template ->
            try {
                val request = template.toRequest()
                val synced = if (pending[template.id] == "PENDING_UPDATE") {
                    workoutApi.updateWorkoutTemplate(request).toDomain()
                } else {
                    workoutApi.createWorkoutTemplate(request).toDomain()
                }
                cacheTemplate(synced, "SYNCED")
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                return
            }
        }
    }

    private fun replaceCache(templates: List<WorkoutTemplate>) {
        database.transaction {
            val queries = database.workoutTemplateCacheQueries
            queries.deleteSyncedTemplateSets()
            queries.deleteSyncedTemplateExercises()
            queries.deleteSyncedWorkoutTemplates()
            templates.forEach { template ->
                cacheTemplate(template, "SYNCED")
            }
        }
    }

    private fun cacheTemplate(template: WorkoutTemplate, status: String) {
        val queries = database.workoutTemplateCacheQueries
        queries.deleteTemplateSetsForTemplate(template.id.toString())
        queries.deleteTemplateExercisesForTemplate(template.id.toString())
        queries.insertCachedWorkoutTemplate(
            template.id.toString(), template.ownerId?.toString(), template.name,
            template.createdAt.toEpochMilliseconds(), template.updatedAt.toEpochMilliseconds(), status,
        )
        template.exercises.forEach { exercise ->
            queries.insertCachedTemplateExercise(
                exercise.id.toString(), template.id.toString(), exercise.exerciseId.toString(),
                exercise.positionInTemplate.toLong(),
            )
            exercise.sets.forEach { set ->
                queries.insertCachedTemplateSet(
                    set.id.toString(), exercise.id.toString(), set.positionInExercise.toLong(),
                    set.targetReps.toLong(), set.targetWeight, set.targetWeightUnit.name,
                )
            }
        }
    }

    private fun readCache(): List<WorkoutTemplate> {
        val queries = database.workoutTemplateCacheQueries
        return queries.selectCachedWorkoutTemplates().executeAsList().map { template ->
            WorkoutTemplate(
                id = Uuid.parse(template.id),
                ownerId = template.owner_id?.let(Uuid::parse),
                name = template.name,
                exercises = queries.selectCachedTemplateExercises(template.id).executeAsList().map { exercise ->
                    TemplateExercise(
                        id = Uuid.parse(exercise.id),
                        exerciseId = Uuid.parse(exercise.exercise_id),
                        positionInTemplate = exercise.position_in_template.toInt(),
                        sets = queries.selectCachedTemplateSets(exercise.id).executeAsList().map { set ->
                            TemplateSet(
                                id = Uuid.parse(set.id),
                                positionInExercise = set.position_in_exercise.toInt(),
                                targetReps = set.target_reps.toInt(),
                                targetWeight = set.target_weight,
                                targetWeightUnit = WeightUnit.valueOf(set.target_weight_unit),
                            )
                        },
                    )
                },
                createdAt = Instant.fromEpochMilliseconds(template.created_at_epoch_milliseconds),
                updatedAt = Instant.fromEpochMilliseconds(template.updated_at_epoch_milliseconds),
            )
        }
    }
}

private fun CreateWorkoutTemplateInput.toTemplate(now: Instant) = WorkoutTemplate(
    id = clientGeneratedId,
    ownerId = Uuid.parse("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
    name = name,
    exercises = exercises.map { exercise ->
        TemplateExercise(
            exercise.id, exercise.exerciseId, exercise.positionInTemplate,
            exercise.sets.map { set ->
                TemplateSet(set.id, set.positionInExercise, set.targetReps, set.targetWeight, set.targetWeightUnit)
            },
        )
    },
    createdAt = now, updatedAt = now,
)

private fun CreateWorkoutTemplateInput.toRequest() = CreateWorkoutTemplateRequestDto(
    clientGeneratedId.toString(), name,
    exercises.map { exercise ->
        CreateTemplateExerciseRequestDto(
            exercise.exerciseId.toString(), exercise.positionInTemplate,
            exercise.sets.map { set ->
                CreateTemplateSetRequestDto(
                    set.positionInExercise, set.targetReps, set.targetWeight, set.targetWeightUnit,
                )
            },
        )
    },
)

private fun WorkoutTemplate.toRequest() = CreateWorkoutTemplateRequestDto(
    id.toString(), name,
    exercises.map { exercise ->
        CreateTemplateExerciseRequestDto(
            exercise.exerciseId.toString(), exercise.positionInTemplate,
            exercise.sets.map { set ->
                CreateTemplateSetRequestDto(
                    set.positionInExercise, set.targetReps, set.targetWeight, set.targetWeightUnit,
                )
            },
        )
    },
)
