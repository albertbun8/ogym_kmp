package com.example.ogym_kmp.feature.workout.domain.model

import com.example.ogym_kmp.core.common.model.WeightUnit
import kotlin.uuid.Uuid

data class CreateWorkoutTemplateInput(
    val clientGeneratedId: Uuid,
    val name: String,
    val exercises: List<CreateTemplateExerciseInput>,
) {
    init {
        require(name.isNotBlank()) { "Template name must not be blank." }
        require(exercises.isNotEmpty()) { "A template must contain at least one exercise." }
    }
}

data class CreateTemplateExerciseInput(
    val id: Uuid,
    val exerciseId: Uuid,
    val positionInTemplate: Int,
    val sets: List<CreateTemplateSetInput>,
)

data class CreateTemplateSetInput(
    val id: Uuid,
    val positionInExercise: Int,
    val targetReps: Int,
    val targetWeight: Double,
    val targetWeightUnit: WeightUnit,
)
