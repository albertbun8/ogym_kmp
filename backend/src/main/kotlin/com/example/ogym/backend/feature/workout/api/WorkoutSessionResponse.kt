package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.domain.WorkoutExercise
import com.example.ogym.backend.feature.workout.domain.WorkoutSession
import com.example.ogym.backend.feature.workout.domain.WorkoutSet
import com.example.ogym.backend.feature.workout.domain.WeightUnit
import java.time.Instant

data class WorkoutSessionResponse(
    val id: String,
    val name: String,
    val ownerId: String?,
    val sourceTemplateId: String?,
    val startedAt: Instant,
    val completedAt: Instant?,
    val exercises: List<WorkoutExerciseResponse>,
    val createdAt: Instant
)

data class WorkoutExerciseResponse(
    val id: String,
    val exerciseId: String?,
    val exerciseName: String,
    val positionInWorkout: Int,
    val sets: List<WorkoutSetResponse>,
)

data class WorkoutSetResponse(
    val id: String,
    val positionInExercise: Int,
    val reps: Int,
    val weight: Double,
    val weightUnit: WeightUnit,
    val completed: Boolean,
)

fun WorkoutSession.toResponse(): WorkoutSessionResponse {
    return WorkoutSessionResponse(
        id = id.toString(),
        name = name,
        ownerId = ownerId?.toString(),
        sourceTemplateId = sourceTemplateId?.toString(),
        startedAt = startedAt,
        completedAt = completedAt,
        exercises = exercises.map { exercise ->
            exercise.toResponse()
        },
        createdAt = createdAt
    )
}

fun WorkoutExercise.toResponse(): WorkoutExerciseResponse {
    return WorkoutExerciseResponse(
        id = id.toString(),
        exerciseId = exerciseId?.toString(),
        exerciseName = exerciseName,
        positionInWorkout = positionInWorkout,
        sets = sets.map { set ->
            set.toResponse()
        }
    )
}

fun WorkoutSet.toResponse(): WorkoutSetResponse {
    return WorkoutSetResponse(
        id = id.toString(),
        positionInExercise = positionInExercise,
        reps = reps,
        weight = weight,
        weightUnit = weightUnit,
        completed = completed
    )
}
