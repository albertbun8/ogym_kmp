package com.example.ogym_kmp.feature.workout.data.mapper

import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutExerciseResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutSessionResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutSetResponseDto
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutExercise
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSet
import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlin.uuid.Uuid

fun WorkoutSessionResponseDto.toDomain(): WorkoutSession {
    return WorkoutSession(
        id = Uuid.parse(this.id),
        name = this.name,
        ownerId = this.ownerId?.let {
            Uuid.parse(it)
        },
        sourceTemplateId = this.sourceTemplateId?.let {
            Uuid.parse(it)
        },
        startedAt = this.startedAt,
        completedAt = this.completedAt,
        exercises = this.exercises.map { it.toDomain() },
        createdAt = this.createdAt
    )
}

fun WorkoutExerciseResponseDto.toDomain(): WorkoutExercise {
    return WorkoutExercise(
        id = Uuid.parse(this.id),
        exerciseId = exerciseId?.let { id ->
            Uuid.parse(id)
        },
        exerciseName = this.exerciseName,
        positionInWorkout = this.positionInWorkout,
        sets = this.sets.map { it.toDomain()}
    )
}

fun WorkoutSetResponseDto.toDomain(): WorkoutSet {
    return WorkoutSet(
        id = Uuid.parse(this.id),
        positionInExercise = this.positionInExercise,
        reps = this.reps,
        weight = this.weight,
        weightUnit = WeightUnit.valueOf(this.weightUnit.name),
        completed = this.completed
    )
}
