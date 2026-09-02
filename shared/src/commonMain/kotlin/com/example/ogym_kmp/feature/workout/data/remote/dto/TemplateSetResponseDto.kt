package com.example.ogym_kmp.feature.workout.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TemplateSetResponseDto(
    val id: String,
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnitDto = WeightUnitDto.LB,
)
