package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository

class GetWorkoutTemplates(
    private val workoutTemplateRepository: WorkoutTemplateRepository
){
    suspend operator fun invoke(): List<WorkoutTemplate> {
        return workoutTemplateRepository.getWorkoutTemplates()
    }
}