package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.uuid.Uuid

class UpdateActiveWorkoutSet(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(
        setId: Uuid,
        reps: Int?,
        weight: Double?,
    ): ActiveWorkoutDraft {
        val activeWorkout = checkNotNull(activeWorkoutRepository.getActiveWorkout()) {
            "An active workout is required before updating a set."
        }
        check(activeWorkout.exercises.any { exercise -> exercise.sets.any { it.id == setId } }) {
            "The set does not belong to the active workout."
        }

        val updatedWorkout = activeWorkout.copy(
            exercises = activeWorkout.exercises.map { exercise ->
                exercise.copy(
                    sets = exercise.sets.map { set ->
                        if (set.id == setId) set.copy(reps = reps, weight = weight) else set
                    },
                )
            },
        )

        activeWorkoutRepository.saveActiveWorkout(updatedWorkout)
        return updatedWorkout
    }
}
