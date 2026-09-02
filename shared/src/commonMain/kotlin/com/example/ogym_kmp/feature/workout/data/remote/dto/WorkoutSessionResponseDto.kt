package com.example.ogym_kmp.feature.workout.data.remote.dto

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class WorkoutSessionResponseDto(
    val id: String,
    val name: String,
    val ownerId: String?,
    val sourceTemplateId: String?,
    val startedAt: Instant,
    val completedAt: Instant?,
    val exercises: List<WorkoutExerciseResponseDto>,
    val createdAt: Instant
)