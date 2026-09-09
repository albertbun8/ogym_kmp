package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.domain.WeightUnit
import java.time.Instant
import java.util.UUID

data class CreateWorkoutSessionRequest(
    val clientGeneratedId: UUID,
    val name: String,
    val sourceTemplateId: UUID?,
    val startedAt: Instant,
    val completedAt: Instant,
    val exercises: List<CreateWorkoutExerciseRequest>,
)

data class CreateWorkoutExerciseRequest(
    val exerciseId: UUID?,
    val exerciseName: String,
    val positionInWorkout: Int,
    val sets: List<CreateWorkoutSetRequest>,
)

data class CreateWorkoutSetRequest(
    val positionInExercise: Int,
    val reps: Int,
    val weight: Double,
    val weightUnit: WeightUnit,
)
