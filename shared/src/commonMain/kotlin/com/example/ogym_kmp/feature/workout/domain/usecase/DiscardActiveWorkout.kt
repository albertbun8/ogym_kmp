package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository

class DiscardActiveWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke() {
        activeWorkoutRepository.clearActiveWorkout()
    }
}
