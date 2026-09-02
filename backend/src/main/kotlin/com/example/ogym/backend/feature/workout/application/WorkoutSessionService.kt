package com.example.ogym.backend.feature.workout.application

import com.example.ogym.backend.feature.workout.domain.WorkoutSession
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutSessionRepository
import org.springframework.stereotype.Service

@Service
class WorkoutSessionService(
    private val workoutSessionsRepository: WorkoutSessionRepository,
) {
    fun getAll(): List<WorkoutSession> {
        return workoutSessionsRepository.findAll()
    }
}
