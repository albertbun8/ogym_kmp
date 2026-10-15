package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

class SetActiveWorkoutSetCompletion(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(
        setId: Uuid,
        completed: Boolean,
        changedAt: Instant,
    ): ActiveWorkoutDraft {
        val activeWorkout = checkNotNull(activeWorkoutRepository.getActiveWorkout()) {
            "An active workout is required before completing a set."
        }
        check(activeWorkout.exercises.any { exercise -> exercise.sets.any { it.id == setId } }) {
            "The set does not belong to the active workout."
        }

        val updatedWorkout = activeWorkout.copy(
            exercises = activeWorkout.exercises.map { exercise ->
                exercise.copy(
                    sets = exercise.sets.map { set ->
                        if (set.id == setId) set.copy(completed = completed) else set
                    },
                )
            },
            restTimerEndsAt = if (completed) {
                changedAt + activeWorkout.restDurationSeconds.seconds
            } else {
                activeWorkout.restTimerEndsAt
            },
        )

        activeWorkoutRepository.saveActiveWorkout(updatedWorkout)
        return updatedWorkout
    }
}
