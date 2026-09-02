package com.example.ogym_kmp.feature.workout.domain.repository

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft

interface ActiveWorkoutRepository {
    suspend fun getActiveWorkout(): ActiveWorkoutDraft?
    suspend fun saveActiveWorkout(draft: ActiveWorkoutDraft)
    suspend fun clearActiveWorkout()
}
