package com.example.ogym.backend.feature.workout.domain

import java.util.UUID
import java.time.Instant

data class WorkoutSession(
    val id: UUID,
    val ownerId: UUID?,
    val name: String,
    val sourceTemplateId: UUID?,
    val startedAt: Instant,
    val completedAt: Instant?,
    val exercises: List<WorkoutExercise>,
    val createdAt: Instant

) {
    init {
        require(name.isNotBlank()) {
            "Workout name must not be blank."
        }
    }
}