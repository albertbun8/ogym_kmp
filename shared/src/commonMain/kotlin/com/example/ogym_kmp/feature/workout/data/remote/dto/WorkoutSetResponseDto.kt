package com.example.ogym_kmp.feature.workout.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSetResponseDto(
    val id: String,
    val positionInExercise: Int,
    val reps: Int,
    val weight: Double,
    val weightUnit: WeightUnitDto = WeightUnitDto.LB,
    val completed: Boolean
) {
}
