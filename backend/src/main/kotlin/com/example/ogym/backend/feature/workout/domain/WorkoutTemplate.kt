package com.example.ogym.backend.feature.workout.domain

import java.util.UUID
import java.time.Instant

data class WorkoutTemplate(
    val id: UUID,
    val ownerId: UUID?,
    val name: String,
    val exercises: List<TemplateExercise>,
    val createdAt: Instant,
    val updatedAt: Instant
) {
}