package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.uuid.Uuid

class RemoveExerciseFromActiveWorkout(
    private val repository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(exerciseId: Uuid): ActiveWorkoutDraft {
        val workout = checkNotNull(repository.getActiveWorkout()) { "No active workout." }
        val updated = workout.copy(
            exercises = workout.exercises
                .filterNot { it.id == exerciseId }
                .mapIndexed { index, exercise -> exercise.copy(positionInWorkout = index) },
        )
        repository.saveActiveWorkout(updated)
        return updated
    }
}
