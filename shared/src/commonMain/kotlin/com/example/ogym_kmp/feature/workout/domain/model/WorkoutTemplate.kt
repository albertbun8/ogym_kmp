package com.example.ogym_kmp.feature.workout.domain.model

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class WorkoutTemplate(
    val id: Uuid,
    val ownerId: Uuid?,
    val name: String,
    val exercises: List<TemplateExercise>,
    val createdAt: Instant,
    val updatedAt: Instant
) {
}