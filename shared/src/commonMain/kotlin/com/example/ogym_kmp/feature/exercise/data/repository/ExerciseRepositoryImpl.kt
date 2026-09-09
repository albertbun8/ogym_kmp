package com.example.ogym_kmp.feature.exercise.data.repository

import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.data.mapper.toDomain
import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.domain.model.MuscleGroup
import kotlinx.coroutines.CancellationException
import kotlin.uuid.Uuid

class ExerciseRepositoryImpl(
    private val exerciseApi: ExerciseApi,
    private val database: OGymDatabase,
) : ExerciseRepository {
    override suspend fun getExercises(): List<Exercise> {
        return try {
            val remoteExercises = exerciseApi.getExercises().map { it.toDomain() }
            database.transaction {
                database.exerciseCacheQueries.deleteCachedExercises()
                remoteExercises.forEach { exercise ->
                    database.exerciseCacheQueries.insertCachedExercise(
                        id = exercise.id.toString(),
                        name = exercise.name,
                        muscle_groups = exercise.muscleGroups.joinToString(",", transform = MuscleGroup::name),
                        owner_id = exercise.ownerId?.toString(),
                    )
                }
            }
            remoteExercises
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            readCachedExercises().ifEmpty { throw exception }
        }
    }

    private fun readCachedExercises(): List<Exercise> =
        database.exerciseCacheQueries.selectCachedExercises().executeAsList().map { exercise ->
            Exercise(
                id = Uuid.parse(exercise.id),
                name = exercise.name,
                muscleGroups = exercise.muscle_groups.split(',').map(MuscleGroup::valueOf),
                ownerId = exercise.owner_id?.let(Uuid::parse),
            )
        }
}
