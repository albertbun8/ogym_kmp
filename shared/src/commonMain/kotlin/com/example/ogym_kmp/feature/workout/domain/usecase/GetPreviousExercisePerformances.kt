package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.feature.workout.domain.model.PreviousSetPerformance
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import kotlin.uuid.Uuid

class GetPreviousExercisePerformances(
    private val workoutSessionRepository: WorkoutSessionRepository,
) {
    suspend operator fun invoke(
        exerciseIds: Set<Uuid>,
    ): Map<Uuid, List<PreviousSetPerformance>> {
        if (exerciseIds.isEmpty()) return emptyMap()

        val completedSessions = workoutSessionRepository.getWorkoutSessions()
            .filter { it.completedAt != null }
            .sortedByDescending { it.completedAt }

        return exerciseIds.mapNotNull { exerciseId ->
            val previousExercise = completedSessions.firstNotNullOfOrNull { session ->
                session.exercises.firstOrNull { it.exerciseId == exerciseId }
            } ?: return@mapNotNull null

            exerciseId to previousExercise.sets
                .filter { it.completed }
                .sortedBy { it.positionInExercise }
                .map { set ->
                    PreviousSetPerformance(
                        positionInExercise = set.positionInExercise,
                        reps = set.reps,
                        weight = set.weight,
                        weightUnit = set.weightUnit,
                    )
                }
        }.toMap()
    }
}
