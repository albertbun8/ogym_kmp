package com.example.ogym.backend.feature.workout.domain

import java.util.UUID

data class WorkoutExercise(
    val id: UUID,
    val exerciseId: UUID?,
    val exerciseName: String,
    val positionInWorkout: Int,
    val sets: List<WorkoutSet>
) {
}