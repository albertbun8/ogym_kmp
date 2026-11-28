package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.core.common.model.convertWeightRounded
import com.example.ogym_kmp.core.preferences.WeightUnitPreferences
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutExerciseDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutSetDraft
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class StartWorkoutFromTemplate(
    private val activeWorkoutRepository: ActiveWorkoutRepository,
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val exerciseRepository: ExerciseRepository,
    private val weightUnitPreferences: WeightUnitPreferences
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        templateId: Uuid,
        startedAt: Instant,
    ): ActiveWorkoutDraft {
        activeWorkoutRepository.getActiveWorkout()?.let { return it }

        val template = checkNotNull(
            workoutTemplateRepository.getWorkoutTemplates().firstOrNull { it.id == templateId },
        ) { "The selected workout template no longer exists." }
        val exercisesById = exerciseRepository.getExercises().associateBy { it.id }

        val preferredWeightUnit = weightUnitPreferences.weightUnit.first()

        val draft = ActiveWorkoutDraft(
            id = Uuid.random(),
            name = template.name,
            sourceTemplateId = template.id,
            startedAt = startedAt,
            exercises = template.exercises
                .sortedBy { it.positionInTemplate }
                .map { templateExercise ->
                    val exercise = checkNotNull(exercisesById[templateExercise.exerciseId]) {
                        "An exercise from the selected template is unavailable."
                    }
                    ActiveWorkoutExerciseDraft(
                        id = Uuid.random(),
                        exerciseId = exercise.id,
                        exerciseName = exercise.name,
                        positionInWorkout = templateExercise.positionInTemplate,
                        muscleGroups = exercise.muscleGroups.toSet(),
                        sets = templateExercise.sets
                            .sortedBy { it.positionInExercise }
                            .map { templateSet ->
                                ActiveWorkoutSetDraft(
                                    id = Uuid.random(),
                                    positionInExercise = templateSet.positionInExercise,
                                    reps = templateSet.targetReps,
                                    weight = convertWeightRounded(
                                        weight = templateSet.targetWeight,
                                        from = templateSet.targetWeightUnit,
                                        to = preferredWeightUnit,
                                    ),
                                    weightUnit = preferredWeightUnit,
                                    completed = false,
                                )
                            },
                    )
                },
            preferredWeightUnit = preferredWeightUnit,
        )

        activeWorkoutRepository.saveActiveWorkout(draft)
        return draft
    }
}
