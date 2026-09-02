package com.example.ogym_kmp.feature.exercise.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseResponseDto(
    val id: String,
    val name: String,
    val muscleGroups: List<String>,
    val ownerId: String?
)