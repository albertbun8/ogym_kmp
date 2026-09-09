package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.workout.data.mapper.toDomain
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutExerciseRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutSessionRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutSetRequestDto
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutExercise
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSet
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.CancellationException
import kotlin.time.Instant
import kotlin.uuid.Uuid

class WorkoutSessionRepositoryImpl(
    private val workoutApi: WorkoutApi,
    private val database: OGymDatabase,
) : WorkoutSessionRepository {
    override suspend fun getWorkoutSessions(): List<WorkoutSession> {
        syncPendingSessions()
        return try {
            val remote = workoutApi.getWorkoutSessions().map { it.toDomain() }
            replaceSyncedCache(remote)
            readCache()
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            readCache().ifEmpty { throw exception }
        }
    }

    override suspend fun createWorkoutSession(
        draft: ActiveWorkoutDraft,
        completedAt: Instant,
    ): WorkoutSession {
        val pending = draft.toPendingSession(completedAt)
        cacheSession(pending, SyncStatus.PENDING_SYNC)
        return try {
            val synced = workoutApi.createWorkoutSession(pending.toRequest()).toDomain()
            cacheSession(synced, SyncStatus.SYNCED)
            synced
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            pending
        }
    }

    private suspend fun syncPendingSessions() {
        for (pending in readCache(SyncStatus.PENDING_SYNC)) {
            try {
                val synced = workoutApi.createWorkoutSession(pending.toRequest()).toDomain()
                cacheSession(synced, SyncStatus.SYNCED)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                return
            }
        }
    }

    private fun replaceSyncedCache(sessions: List<WorkoutSession>) {
        database.transaction {
            val queries = database.workoutSessionCacheQueries
            queries.deleteSyncedWorkoutSets()
            queries.deleteSyncedWorkoutExercises()
            queries.deleteSyncedWorkoutSessions()
            sessions.forEach { cacheSession(it, SyncStatus.SYNCED) }
        }
    }

    private fun cacheSession(session: WorkoutSession, status: SyncStatus) {
        val queries = database.workoutSessionCacheQueries
        database.transaction {
            queries.deleteWorkoutSetsForSession(session.id.toString())
            queries.deleteWorkoutExercisesForSession(session.id.toString())
            queries.deleteWorkoutSession(session.id.toString())
            queries.insertCachedWorkoutSession(
                session.id.toString(), session.ownerId?.toString(), session.name,
                session.sourceTemplateId?.toString(), session.startedAt.toEpochMilliseconds(),
                session.completedAt?.toEpochMilliseconds(), session.createdAt.toEpochMilliseconds(), status.name,
            )
            session.exercises.forEach { exercise ->
                queries.insertCachedWorkoutExercise(
                    exercise.id.toString(), session.id.toString(), exercise.exerciseId?.toString(),
                    exercise.exerciseName, exercise.positionInWorkout.toLong(),
                )
                exercise.sets.forEach { set ->
                    queries.insertCachedWorkoutSet(
                        set.id.toString(), exercise.id.toString(), set.positionInExercise.toLong(),
                        set.reps.toLong(), set.weight, set.weightUnit.name, if (set.completed) 1L else 0L,
                    )
                }
            }
        }
    }

    private fun readCache(status: SyncStatus? = null): List<WorkoutSession> {
        val queries = database.workoutSessionCacheQueries
        val rows = if (status == SyncStatus.PENDING_SYNC) {
            queries.selectPendingWorkoutSessions().executeAsList()
        } else queries.selectCachedWorkoutSessions().executeAsList()
        return rows.map { session ->
            WorkoutSession(
                id = Uuid.parse(session.id), ownerId = session.owner_id?.let(Uuid::parse), name = session.name,
                sourceTemplateId = session.source_template_id?.let(Uuid::parse),
                startedAt = Instant.fromEpochMilliseconds(session.started_at_epoch_milliseconds),
                completedAt = session.completed_at_epoch_milliseconds?.let(Instant::fromEpochMilliseconds),
                exercises = queries.selectCachedWorkoutExercises(session.id).executeAsList().map { exercise ->
                    WorkoutExercise(
                        id = Uuid.parse(exercise.id), exerciseId = exercise.exercise_id?.let(Uuid::parse),
                        exerciseName = exercise.exercise_name, positionInWorkout = exercise.position_in_workout.toInt(),
                        sets = queries.selectCachedWorkoutSets(exercise.id).executeAsList().map { set ->
                            WorkoutSet(
                                Uuid.parse(set.id), set.position_in_exercise.toInt(), set.reps.toInt(), set.weight,
                                WeightUnit.valueOf(set.weight_unit), set.completed == 1L,
                            )
                        },
                    )
                },
                createdAt = Instant.fromEpochMilliseconds(session.created_at_epoch_milliseconds),
            )
        }
    }
}

private enum class SyncStatus { PENDING_SYNC, SYNCED }

private fun ActiveWorkoutDraft.toPendingSession(completedAt: Instant) = WorkoutSession(
    id, null, name, sourceTemplateId, startedAt, completedAt,
    exercises.map { exercise ->
        WorkoutExercise(
            exercise.id, exercise.exerciseId, exercise.exerciseName, exercise.positionInWorkout,
            exercise.sets.filter { it.completed }.map { set ->
                WorkoutSet(
                    set.id, set.positionInExercise, checkNotNull(set.reps), checkNotNull(set.weight),
                    set.weightUnit, true,
                )
            },
        )
    },
    completedAt,
)

private fun WorkoutSession.toRequest() = CreateWorkoutSessionRequestDto(
    clientGeneratedId = id.toString(), name = name, sourceTemplateId = sourceTemplateId?.toString(),
    startedAt = startedAt, completedAt = checkNotNull(completedAt),
    exercises = exercises.map { exercise ->
        CreateWorkoutExerciseRequestDto(
            exerciseId = checkNotNull(exercise.exerciseId).toString(), exerciseName = exercise.exerciseName,
            positionInWorkout = exercise.positionInWorkout,
            sets = exercise.sets.filter { it.completed }.map { set ->
                CreateWorkoutSetRequestDto(set.positionInExercise, set.reps, set.weight, set.weightUnit)
            },
        )
    },
)
