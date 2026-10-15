package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.domain.model.MuscleGroup
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutExerciseDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutSetDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.time.Instant
import kotlin.uuid.Uuid

class SqlDelightActiveWorkoutRepository(
    private val database: OGymDatabase,
) : ActiveWorkoutRepository {
    private val queries = database.activeWorkoutQueries

    override suspend fun getActiveWorkout(): ActiveWorkoutDraft? {
        val workout = queries.selectActiveWorkout().executeAsOneOrNull() ?: return null
        val exercises = queries.selectActiveWorkoutExercises(workout.id).executeAsList().map { exercise ->
            ActiveWorkoutExerciseDraft(
                id = Uuid.parse(exercise.id),
                exerciseId = Uuid.parse(exercise.exercise_id),
                exerciseName = exercise.exercise_name,
                positionInWorkout = exercise.position_in_workout.toInt(),
                muscleGroups = exercise.muscle_groups
                    .split(',')
                    .filter(String::isNotBlank)
                    .mapTo(mutableSetOf(), MuscleGroup::valueOf),
                sets = queries.selectActiveWorkoutSets(exercise.id).executeAsList().map { set ->
                    ActiveWorkoutSetDraft(
                        id = Uuid.parse(set.id),
                        positionInExercise = set.position_in_exercise.toInt(),
                        reps = set.reps?.toInt(),
                        weight = set.weight,
                        weightUnit = WeightUnit.valueOf(set.weight_unit),
                        completed = set.completed == 1L,
                    )
                },
            )
        }

        return ActiveWorkoutDraft(
            id = Uuid.parse(workout.id),
            name = workout.name,
            sourceTemplateId = workout.source_template_id?.let(Uuid::parse),
            startedAt = Instant.fromEpochMilliseconds(workout.started_at_epoch_milliseconds),
            exercises = exercises,
            preferredWeightUnit = WeightUnit.valueOf(workout.preferred_weight_unit),
            restDurationSeconds = workout.rest_duration_seconds.toInt(),
            restTimerEndsAt = workout.rest_timer_ends_at_epoch_milliseconds
                ?.let(Instant::fromEpochMilliseconds),
        )
    }

    override suspend fun saveActiveWorkout(draft: ActiveWorkoutDraft) {
        database.transaction {
            queries.deleteAllActiveWorkoutSets()
            queries.deleteAllActiveWorkoutExercises()
            queries.deleteActiveWorkout()
            queries.insertActiveWorkout(
                id = draft.id.toString(),
                name = draft.name,
                source_template_id = draft.sourceTemplateId?.toString(),
                started_at_epoch_milliseconds = draft.startedAt.toEpochMilliseconds(),
                preferred_weight_unit = draft.preferredWeightUnit.name,
                rest_duration_seconds = draft.restDurationSeconds.toLong(),
                rest_timer_ends_at_epoch_milliseconds = draft.restTimerEndsAt?.toEpochMilliseconds(),
            )
            draft.exercises.forEach { exercise ->
                queries.insertActiveWorkoutExercise(
                    id = exercise.id.toString(),
                    workout_id = draft.id.toString(),
                    exercise_id = exercise.exerciseId.toString(),
                    exercise_name = exercise.exerciseName,
                    position_in_workout = exercise.positionInWorkout.toLong(),
                    muscle_groups = exercise.muscleGroups.joinToString(",", transform = MuscleGroup::name),
                )
                exercise.sets.forEach { set ->
                    queries.insertActiveWorkoutSet(
                        id = set.id.toString(),
                        workout_exercise_id = exercise.id.toString(),
                        position_in_exercise = set.positionInExercise.toLong(),
                        reps = set.reps?.toLong(),
                        weight = set.weight,
                        weight_unit = set.weightUnit.name,
                        completed = if (set.completed) 1L else 0L,
                    )
                }
            }
        }
    }

    override suspend fun clearActiveWorkout() {
        database.transaction {
            queries.deleteAllActiveWorkoutSets()
            queries.deleteAllActiveWorkoutExercises()
            queries.deleteActiveWorkout()
        }
    }
}
