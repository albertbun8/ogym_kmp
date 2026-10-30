package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import kotlin.uuid.Uuid

class GetWorkoutTemplate(
    private val workoutTemplateRepository: WorkoutTemplateRepository
){
    suspend operator fun invoke(workoutTemplateId: Uuid): WorkoutTemplate? {
        return workoutTemplateRepository.getWorkoutTemplate(workoutTemplateId)
    }
}