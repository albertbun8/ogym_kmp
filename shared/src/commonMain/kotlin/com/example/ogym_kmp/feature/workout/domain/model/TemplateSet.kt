package com.example.ogym_kmp.feature.workout.domain.model

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlin.uuid.Uuid

data class TemplateSet(
    val id: Uuid,
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnit = WeightUnit.LB,
) {
}
