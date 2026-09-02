package com.example.ogym.backend.feature.exercise.domain

interface ExerciseRepository {
    fun findAll(): List<Exercise>
}