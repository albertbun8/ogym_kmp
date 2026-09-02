package com.example.ogym.backend.feature.workout.domain

import java.util.UUID

data class WorkoutSet(
    val id: UUID,
    val positionInExercise: Int,
    val reps: Int,
    val weight: Double,
    val weightUnit: WeightUnit = WeightUnit.LB,
    val completed: Boolean
) {
}
