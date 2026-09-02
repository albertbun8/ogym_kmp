package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.feature.workout.data.mapper.toDomain
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository

class WorkoutTemplateRepositoryImpl(
    private val workoutApi: WorkoutApi
) : WorkoutTemplateRepository {
    override suspend fun getWorkoutTemplates(): List<WorkoutTemplate> {
        return workoutApi.getWorkoutTemplates().map { dto ->
            dto.toDomain()
        }
    }
}