package com.example.ogym_kmp.feature.workout.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutExerciseResponseDto(
    val id: String,
    val exerciseId: String?,
    val exerciseName: String,
    val positionInWorkout: Int,
    val sets: List<WorkoutSetResponseDto>
) {
}