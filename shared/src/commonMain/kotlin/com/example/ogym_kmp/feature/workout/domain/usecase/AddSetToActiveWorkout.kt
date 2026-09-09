package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutSetDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.uuid.Uuid

class AddSetToActiveWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(
        exerciseDraftId: Uuid,
        setId: Uuid,
    ): ActiveWorkoutDraft {
        val activeWorkout = checkNotNull(activeWorkoutRepository.getActiveWorkout()) {
            "An active workout is required before adding a set."
        }
        check(activeWorkout.exercises.any { it.id == exerciseDraftId }) {
            "The exercise does not belong to the active workout."
        }

        val updatedWorkout = activeWorkout.copy(
            exercises = activeWorkout.exercises.map { exercise ->
                if (exercise.id != exerciseDraftId) return@map exercise

                val previousSet = exercise.sets.lastOrNull()

                exercise.copy(
                    sets = exercise.sets + ActiveWorkoutSetDraft(
                        id = setId,
                        positionInExercise = exercise.sets.size,
                        reps = previousSet?.reps ?: 0,
                        weight = previousSet?.weight ?: 0.0,
                        weightUnit = previousSet?.weightUnit ?: activeWorkout.preferredWeightUnit,
                        completed = false,
                    ),
                )
            },
        )

        activeWorkoutRepository.saveActiveWorkout(updatedWorkout)
        return updatedWorkout
    }
}
