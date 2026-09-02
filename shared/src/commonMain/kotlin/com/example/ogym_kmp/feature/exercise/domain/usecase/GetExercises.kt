package com.example.ogym_kmp.feature.exercise.domain.usecase

import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository

class GetExercises(
    private val exerciseRepository: ExerciseRepository
){
    suspend operator fun invoke(): List<Exercise> {
        return exerciseRepository.getExercises()
    }
}