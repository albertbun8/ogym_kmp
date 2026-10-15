package com.example.ogym_kmp.feature.workout.data.remote.dto

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class CreateWorkoutSessionRequestDto(
    val clientGeneratedId: String,
    val name: String,
    val sourceTemplateId: String?,
    val startedAt: Instant,
    val completedAt: Instant,
    val exercises: List<CreateWorkoutExerciseRequestDto>,
)

@Serializable
data class CreateWorkoutExerciseRequestDto(
    val exerciseId: String,
    val exerciseName: String,
    val positionInWorkout: Int,
    val sets: List<CreateWorkoutSetRequestDto>,
)

@Serializable
data class CreateWorkoutSetRequestDto(
    val positionInExercise: Int,
    val reps: Int,
    val weight: Double,
    val weightUnit: WeightUnit,
)
