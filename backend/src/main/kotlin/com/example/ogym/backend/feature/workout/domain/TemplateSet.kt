package com.example.ogym.backend.feature.workout.domain

import java.util.UUID

data class TemplateSet(
    val id: UUID,
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnit = WeightUnit.LB,
) {
}
