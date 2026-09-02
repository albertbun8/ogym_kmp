package com.example.ogym_kmp.feature.workout.data.mapper

import com.example.ogym_kmp.feature.workout.data.remote.dto.TemplateExerciseResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.TemplateSetResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutTemplateResponseDto
import com.example.ogym_kmp.feature.workout.domain.model.TemplateExercise
import com.example.ogym_kmp.feature.workout.domain.model.TemplateSet
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlin.uuid.Uuid

fun WorkoutTemplateResponseDto.toDomain(): WorkoutTemplate {
    return WorkoutTemplate(
        id = Uuid.parse(this.id),
        ownerId = this.ownerId?.let {
            Uuid.parse(it)
        },
        name = this.name,
        exercises = this.exercises.map { it.toDomain() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun TemplateExerciseResponseDto.toDomain(): TemplateExercise{
    return TemplateExercise(
        id = Uuid.parse(this.id),
        exerciseId = Uuid.parse(this.exerciseId),
        positionInTemplate = this.positionInTemplate,
        sets = this.sets.map { it.toDomain() }
    )
}

fun TemplateSetResponseDto.toDomain(): TemplateSet{
    return TemplateSet(
        id = Uuid.parse(this.id),
        positionInExercise = this.positionInExercise,
        targetReps = this.targetReps,
        targetWeight = this.targetWeight,
        targetWeightUnit = WeightUnit.valueOf(this.targetWeightUnit.name),
        )
}
