package com.example.ogym.backend.feature.exercise.application

import com.example.ogym.backend.feature.exercise.domain.Exercise
import com.example.ogym.backend.feature.exercise.domain.ExerciseRepository
import org.springframework.stereotype.Service

@Service
class ExerciseService(
    private val exerciseRepository: ExerciseRepository
) {
    fun getAll(): List<Exercise> {
        return exerciseRepository.findAll()
    }
}