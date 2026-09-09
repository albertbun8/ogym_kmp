package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository

class GetActiveWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
) {
    suspend operator fun invoke(): ActiveWorkoutDraft? =
        activeWorkoutRepository.getActiveWorkout()
}
