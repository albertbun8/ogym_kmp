package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import kotlin.time.Instant

class FinishActiveWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
) {
    suspend operator fun invoke(completedAt: Instant): WorkoutSession {
        val draft = checkNotNull(activeWorkoutRepository.getActiveWorkout()) {
            "There is no active workout to finish."
        }
        require(draft.exercises.isNotEmpty()) {
            "Add at least one exercise before finishing the workout."
        }

        val session = workoutSessionRepository.createWorkoutSession(draft, completedAt)
        activeWorkoutRepository.clearActiveWorkout()
        return session
    }
}
