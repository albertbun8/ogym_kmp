package com.example.ogym_kmp.feature.workout.domain.repository

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate

interface WorkoutTemplateRepository {
    suspend fun getWorkoutTemplates(): List<WorkoutTemplate>
}