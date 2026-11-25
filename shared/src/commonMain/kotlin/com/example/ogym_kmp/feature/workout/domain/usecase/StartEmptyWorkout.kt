package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.core.preferences.WeightUnitPreferences
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Instant
import kotlin.uuid.Uuid

class StartEmptyWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
    private val weightUnitPreferences: WeightUnitPreferences
) {
    suspend operator fun invoke(
        id: Uuid,
        startedAt: Instant,
        localDate: String,
    ): ActiveWorkoutDraft {
        val existingDraft = activeWorkoutRepository.getActiveWorkout()

        if (existingDraft != null) {
            return existingDraft
        }

        val preferredWeightUnit = weightUnitPreferences.weightUnit.first()

        val emptyActiveWorkout = ActiveWorkoutDraft(
            id = id,
            name = "Empty Workout $localDate",
            sourceTemplateId = null,
            startedAt = startedAt,
            exercises = emptyList(),
            preferredWeightUnit = preferredWeightUnit
        )

        activeWorkoutRepository.saveActiveWorkout(emptyActiveWorkout)

        return emptyActiveWorkout
    }
}