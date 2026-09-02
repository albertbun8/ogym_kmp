package com.example.ogym.backend.feature.workout.application

import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutTemplateRepository
import org.springframework.stereotype.Service

@Service
class WorkoutTemplateService(
    private val workoutTemplateRepository: WorkoutTemplateRepository
) {
    fun getAll(): List<WorkoutTemplate> {
        return workoutTemplateRepository.findAll()
    }
}
