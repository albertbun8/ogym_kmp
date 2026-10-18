package com.example.ogym_kmp.feature.workout.domain.repository

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.model.CreateWorkoutTemplateInput
import kotlin.time.Instant
import kotlin.uuid.Uuid

interface WorkoutTemplateRepository {
    suspend fun getWorkoutTemplates(): List<WorkoutTemplate>
    suspend fun getWorkoutTemplate(id: Uuid): WorkoutTemplate?
    suspend fun createWorkoutTemplate(input: CreateWorkoutTemplateInput, createdAt: Instant): WorkoutTemplate
    suspend fun updateWorkoutTemplate(input: CreateWorkoutTemplateInput, updatedAt: Instant): WorkoutTemplate
    suspend fun deleteWorkoutTemplate(id: kotlin.uuid.Uuid)
}
