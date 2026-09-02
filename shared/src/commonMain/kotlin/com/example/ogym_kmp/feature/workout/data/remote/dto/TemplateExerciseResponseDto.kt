package com.example.ogym_kmp.feature.workout.data.remote.dto


import kotlinx.serialization.Serializable

@Serializable
data class TemplateExerciseResponseDto(
    val id: String,
    val exerciseId: String,
    val positionInTemplate: Int,
    val sets: List<TemplateSetResponseDto>
)