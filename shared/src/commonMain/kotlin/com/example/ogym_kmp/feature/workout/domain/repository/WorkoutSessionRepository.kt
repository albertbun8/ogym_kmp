package com.example.ogym_kmp.feature.workout.domain.repository

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession

interface WorkoutSessionRepository {
    suspend fun getWorkoutSessions(): List<WorkoutSession>
}