package com.example.ogym_kmp.feature.workout.domain.model

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.exercise.domain.model.MuscleGroup
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class ActiveWorkoutDraft(
    val id: Uuid,
    val name: String,
    val sourceTemplateId: Uuid?,
    val startedAt: Instant,
    val exercises: List<ActiveWorkoutExerciseDraft>,
    val preferredWeightUnit: WeightUnit = WeightUnit.LB,
    val restDurationSeconds: Int = 90,
    val restTimerEndsAt: Instant? = null,
) {
    init {
        require(name.isNotBlank()) { "Active workout name must not be blank." }
        require(restDurationSeconds > 0) { "Rest duration must be positive." }
    }

    val targetedMuscleGroups: Set<MuscleGroup>
        get() = exercises.flatMapTo(mutableSetOf()) { it.muscleGroups }
}

data class ActiveWorkoutExerciseDraft(
    val id: Uuid,
    val exerciseId: Uuid,
    val exerciseName: String,
    val positionInWorkout: Int,
    val muscleGroups: Set<MuscleGroup>,
    val sets: List<ActiveWorkoutSetDraft>,
) {
    init {
        require(exerciseName.isNotBlank()) { "Exercise name must not be blank." }
        require(positionInWorkout >= 0) { "Exercise position must not be negative." }
        require(muscleGroups.isNotEmpty()) { "An active exercise must target at least one muscle group." }
    }
}

data class ActiveWorkoutSetDraft(
    val id: Uuid,
    val positionInExercise: Int,
    val reps: Int?,
    val weight: Double?,
    val weightUnit: WeightUnit,
    val completed: Boolean,
) {
    init {
        require(positionInExercise >= 0) { "Set position must not be negative." }
        require(reps == null || reps >= 0) { "Repetitions must not be negative." }
        require(weight == null || weight >= 0.0) { "Weight must not be negative." }
        require(!completed || (reps != null && weight != null)) {
            "A completed set must have repetitions and weight."
        }
    }
}
