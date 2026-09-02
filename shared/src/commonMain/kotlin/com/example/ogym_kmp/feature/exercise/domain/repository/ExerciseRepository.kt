package com.example.ogym_kmp.feature.exercise.domain.repository

import com.example.ogym_kmp.feature.exercise.domain.model.Exercise

interface ExerciseRepository {
    suspend fun getExercises(): List<Exercise>
}