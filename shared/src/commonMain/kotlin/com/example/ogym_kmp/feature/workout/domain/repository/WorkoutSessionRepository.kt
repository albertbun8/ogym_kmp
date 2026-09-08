package com.example.ogym_kmp.feature.workout.domain.repository

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import kotlin.time.Instant

interface WorkoutSessionRepository {
    suspend fun getWorkoutSessions(): List<WorkoutSession>
    suspend fun createWorkoutSession(
        draft: ActiveWorkoutDraft,
        completedAt: Instant,
    ): WorkoutSession
}
