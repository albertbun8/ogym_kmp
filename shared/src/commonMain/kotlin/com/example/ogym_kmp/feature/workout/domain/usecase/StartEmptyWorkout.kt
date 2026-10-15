package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import kotlin.time.Instant
import kotlin.uuid.Uuid

class StartEmptyWorkout(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
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

        val emptyActiveWorkout = ActiveWorkoutDraft(
            id = id,
            name = "Empty Workout $localDate",
            sourceTemplateId = null,
            startedAt = startedAt,
            exercises = emptyList(),
        )

        activeWorkoutRepository.saveActiveWorkout(emptyActiveWorkout)

        return emptyActiveWorkout
    }
}