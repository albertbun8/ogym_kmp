package com.example.ogym_kmp.feature.workout.domain.model

import kotlin.uuid.Uuid

data class WorkoutExercise(
    val id: Uuid,
    val exerciseId: Uuid?,
    val exerciseName: String,
    val positionInWorkout: Int,
    val sets: List<WorkoutSet>
) {
}