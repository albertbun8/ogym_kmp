package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutExerciseDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.uuid.Uuid

class AddExerciseToActiveWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(
        draftExerciseId: Uuid,
        exercise: Exercise,
    ): ActiveWorkoutDraft {
        val activeWorkout = checkNotNull(activeWorkoutRepository.getActiveWorkout()) {
            "An active workout is required before adding an exercise."
        }

        val exerciseDraft = ActiveWorkoutExerciseDraft(
            id = draftExerciseId,
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            positionInWorkout = activeWorkout.exercises.size,
            muscleGroups = exercise.muscleGroups.toSet(),
            sets = emptyList(),
        )
        val updatedWorkout = activeWorkout.copy(
            exercises = activeWorkout.exercises + exerciseDraft,
        )

        activeWorkoutRepository.saveActiveWorkout(updatedWorkout)
        return updatedWorkout
    }
}
