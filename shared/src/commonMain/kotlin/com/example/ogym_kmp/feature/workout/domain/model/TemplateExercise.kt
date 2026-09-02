package com.example.ogym_kmp.feature.workout.domain.model

import kotlin.uuid.Uuid

data class TemplateExercise(
    val id: Uuid,
    val exerciseId: Uuid,
    val positionInTemplate: Int,
    val sets: List<TemplateSet>
) {
}