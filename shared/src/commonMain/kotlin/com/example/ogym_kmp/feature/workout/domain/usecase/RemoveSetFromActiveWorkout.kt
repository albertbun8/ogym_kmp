package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.uuid.Uuid

class RemoveSetFromActiveWorkout(
    private val repository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(setId: Uuid): ActiveWorkoutDraft {
        val workout = checkNotNull(repository.getActiveWorkout()) { "No active workout." }
        val updated = workout.copy(
            exercises = workout.exercises.map { exercise ->
                exercise.copy(
                    sets = exercise.sets
                        .filterNot { it.id == setId }
                        .mapIndexed { index, set -> set.copy(positionInExercise = index) },
                )
            },
        )
        repository.saveActiveWorkout(updated)
        return updated
    }
}
