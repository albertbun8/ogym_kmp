package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.domain.WeightUnit
import java.util.UUID

data class CreateWorkoutTemplateRequest(
    val clientGeneratedId: UUID,
    val name: String,
    val exercises: List<CreateTemplateExerciseRequest>,
)

data class CreateTemplateExerciseRequest(
    val exerciseId: UUID,
    val positionInTemplate: Int,
    val sets: List<CreateTemplateSetRequest>,
)

data class CreateTemplateSetRequest(
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnit,
)
