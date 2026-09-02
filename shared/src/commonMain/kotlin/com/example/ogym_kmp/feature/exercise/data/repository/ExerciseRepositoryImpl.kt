package com.example.ogym_kmp.feature.exercise.data.repository

import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.data.mapper.toDomain
import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository

class ExerciseRepositoryImpl(
    private val exerciseApi: ExerciseApi
) : ExerciseRepository {
    override suspend fun getExercises(): List<Exercise> {
        return exerciseApi.getExercises().map { dto ->
            dto.toDomain()
        }
    }
}