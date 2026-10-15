package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import kotlin.uuid.Uuid

class DeleteWorkoutTemplate(private val repository: WorkoutTemplateRepository) {
    suspend operator fun invoke(id: Uuid) = repository.deleteWorkoutTemplate(id)
}
