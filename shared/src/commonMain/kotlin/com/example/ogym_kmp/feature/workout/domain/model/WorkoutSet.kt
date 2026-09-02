package com.example.ogym_kmp.feature.workout.domain.model

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlin.uuid.Uuid

data class WorkoutSet(
    val id: Uuid,
    val positionInExercise: Int,
    val reps: Int,
    val weight: Double,
    val weightUnit: WeightUnit = WeightUnit.LB,
    val completed: Boolean
) {
}
