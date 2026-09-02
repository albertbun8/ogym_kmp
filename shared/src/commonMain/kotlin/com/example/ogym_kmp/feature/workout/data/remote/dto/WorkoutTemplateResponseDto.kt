package com.example.ogym_kmp.feature.workout.data.remote.dto

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class WorkoutTemplateResponseDto (
    val id: String,
    val ownerId: String?,
    val name: String,
    val exercises: List<TemplateExerciseResponseDto>,
    val createdAt: Instant,
    val updatedAt: Instant
){
}