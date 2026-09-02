package com.example.ogym.backend.feature.workout.domain

import java.util.UUID

data class TemplateExercise(
    val id: UUID,
    val exerciseId: UUID,
    val positionInTemplate: Int,
    val sets: List<TemplateSet>
) {
}