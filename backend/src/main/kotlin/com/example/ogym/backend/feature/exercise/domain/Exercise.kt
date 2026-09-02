package com.example.ogym.backend.feature.exercise.domain

import java.util.UUID

data class Exercise(
    val id: UUID,
    val name: String,
    val muscleGroups: Set<MuscleGroup>,
    val ownerId: UUID? = null
) {
    init {
        require(name.isNotBlank()) {
            "Exercise name must not be blank."
        }
        require(muscleGroups.isNotEmpty()) {
            "Exercise must have at least one muscle group."
        }
    }
}