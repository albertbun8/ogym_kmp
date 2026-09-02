package com.example.ogym_kmp.feature.workout.domain.model

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class WorkoutSession(
    val id: Uuid,
    val ownerId: Uuid? = null,
    val name: String,
    val sourceTemplateId: Uuid?,
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