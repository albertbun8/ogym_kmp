package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository

class InMemoryActiveWorkoutRepository : ActiveWorkoutRepository {
    private var activeWorkout: ActiveWorkoutDraft? = null

    override suspend fun getActiveWorkout(): ActiveWorkoutDraft? {
        return activeWorkout
    }

    override suspend fun saveActiveWorkout(draft: ActiveWorkoutDraft) {
        activeWorkout = draft
    }

    override suspend fun clearActiveWorkout() {
        activeWorkout = null
    }
}
