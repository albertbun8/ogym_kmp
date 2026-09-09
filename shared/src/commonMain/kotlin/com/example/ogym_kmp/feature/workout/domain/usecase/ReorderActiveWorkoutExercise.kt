package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.uuid.Uuid

class ReorderActiveWorkoutExercise(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(exerciseId: Uuid, targetExerciseId: Uuid): ActiveWorkoutDraft {
        val workout = checkNotNull(activeWorkoutRepository.getActiveWorkout()) {
            "No active workout to reorder."
        }
        val exercises = workout.exercises.sortedBy { it.positionInWorkout }.toMutableList()
        val fromIndex = exercises.indexOfFirst { it.id == exerciseId }
        val targetIndex = exercises.indexOfFirst { it.id == targetExerciseId }
        require(fromIndex >= 0 && targetIndex >= 0) { "Exercise not found in the active workout." }

        val movedExercise = exercises.removeAt(fromIndex)
        exercises.add(targetIndex, movedExercise)
        val updatedWorkout = workout.copy(
            exercises = exercises.mapIndexed { index, exercise ->
                exercise.copy(positionInWorkout = index)
            },
        )
        activeWorkoutRepository.saveActiveWorkout(updatedWorkout)
        return updatedWorkout
    }
}
