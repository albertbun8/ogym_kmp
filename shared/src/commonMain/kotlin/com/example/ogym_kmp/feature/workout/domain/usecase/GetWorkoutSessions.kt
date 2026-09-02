package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository

class GetWorkoutSessions(
    private val workoutSessionRepository: WorkoutSessionRepository
){
    suspend operator fun invoke(): List<WorkoutSession> {
        return workoutSessionRepository.getWorkoutSessions()
    }
}