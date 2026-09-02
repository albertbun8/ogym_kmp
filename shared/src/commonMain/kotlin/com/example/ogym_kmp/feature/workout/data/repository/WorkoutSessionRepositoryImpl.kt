package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.feature.workout.data.mapper.toDomain
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository

class WorkoutSessionRepositoryImpl(
    private val workoutApi: WorkoutApi
) : WorkoutSessionRepository {
    override suspend fun getWorkoutSessions(): List<WorkoutSession> {
        return workoutApi.getWorkoutSessions().map { dto ->
            dto.toDomain()
        }
    }
}
