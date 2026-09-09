package com.example.ogym_kmp.feature.workout.data.remote.dto

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkoutTemplateRequestDto(
    val clientGeneratedId: String,
    val name: String,
    val exercises: List<CreateTemplateExerciseRequestDto>,
)

@Serializable
data class CreateTemplateExerciseRequestDto(
    val exerciseId: String,
    val positionInTemplate: Int,
    val sets: List<CreateTemplateSetRequestDto>,
)

@Serializable
data class CreateTemplateSetRequestDto(
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnit,
)
