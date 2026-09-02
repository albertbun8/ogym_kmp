package com.example.ogym_kmp.feature.exercise.data.mapper

import com.example.ogym_kmp.feature.exercise.data.remote.dto.ExerciseResponseDto
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.domain.model.MuscleGroup
import kotlin.uuid.Uuid

fun ExerciseResponseDto.toDomain(): Exercise {
    return Exercise(
        id = Uuid.parse(this.id),
        name = this.name,
        muscleGroups = this.muscleGroups.map {
            MuscleGroup.valueOf(it)
        },
        ownerId = this.ownerId?.let {
            Uuid.parse(it)
        }
    )
}