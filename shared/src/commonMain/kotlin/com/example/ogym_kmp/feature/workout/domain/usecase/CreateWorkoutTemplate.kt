package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.CreateWorkoutTemplateInput
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import kotlin.time.Instant

class CreateWorkoutTemplate(private val repository: WorkoutTemplateRepository) {
    suspend operator fun invoke(input: CreateWorkoutTemplateInput, createdAt: Instant): WorkoutTemplate =
        repository.createWorkoutTemplate(input, createdAt)
}
