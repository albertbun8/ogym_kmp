package com.example.ogym.backend.feature.exercise.api

import com.example.ogym.backend.feature.exercise.domain.Exercise

data class ExerciseResponse(
    val id: String,
    val name: String,
    val muscleGroups: List<String>,
    val ownerId: String?
)

fun Exercise.toResponse(): ExerciseResponse {
    return ExerciseResponse(
        id = id.toString(),
        name = name,
        muscleGroups = muscleGroups.map { it.name },
        ownerId = ownerId?.toString()
    )
}