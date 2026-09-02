package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.domain.TemplateExercise
import com.example.ogym.backend.feature.workout.domain.TemplateSet
import com.example.ogym.backend.feature.workout.domain.WorkoutSession
import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.WeightUnit
import java.time.Instant

data class WorkoutTemplateResponse(
    val id: String,
    val name: String,
    val ownerId: String?,
    val exercises: List<TemplateExerciseResponse>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class TemplateExerciseResponse(
    val id: String,
    val exerciseId: String,
    val positionInTemplate: Int,
    val sets: List<TemplateSetResponse>
)

data class TemplateSetResponse(
    val id: String,
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnit,
)

fun WorkoutTemplate.toResponse(): WorkoutTemplateResponse {
    return WorkoutTemplateResponse(
        id = id.toString(),
        name = name,
        ownerId = ownerId?.toString(),
        exercises = exercises.map { it.toResponse() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TemplateExercise.toResponse(): TemplateExerciseResponse {
    return TemplateExerciseResponse(
        id = id.toString(),
        exerciseId = exerciseId.toString(),
        positionInTemplate = positionInTemplate,
        sets = sets.map { it.toResponse() },
    )
}

fun TemplateSet.toResponse(): TemplateSetResponse {
    return TemplateSetResponse(
        id = id.toString(),
        positionInExercise = positionInExercise,
        targetReps = targetReps,
        targetWeight = targetWeight,
        targetWeightUnit = targetWeightUnit,
    )
}
