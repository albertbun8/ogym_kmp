package com.example.ogym_kmp.feature.workout.domain.model

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.exercise.domain.model.MuscleGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ActiveWorkoutDraftTest {

    @Test
    fun emptyActiveWorkoutIsValid() {
        val draft = activeWorkout(exercises = emptyList())

        assertEquals(emptyList(), draft.exercises)
        assertEquals(WeightUnit.LB, draft.preferredWeightUnit)
        assertNull(draft.restTimerEndsAt)
    }

    @Test
    fun completedSetWithoutRepetitionsIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            activeSet(reps = null, weight = 45.0, completed = true)
        }
    }

    @Test
    fun completedSetWithoutWeightIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            activeSet(reps = 8, weight = null, completed = true)
        }
    }

    @Test
    fun incompleteSetWithoutValuesIsValid() {
        val set = activeSet(reps = null, weight = null, completed = false)

        assertNull(set.reps)
        assertNull(set.weight)
    }

    @Test
    fun negativeWeightIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            activeSet(reps = 8, weight = -1.0, completed = false)
        }
    }

    @Test
    fun exerciseWithoutMuscleGroupsIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            activeExercise(muscleGroups = emptySet())
        }
    }

    @Test
    fun targetedMuscleGroupsAreDistinctAcrossExercises() {
        val draft = activeWorkout(
            exercises = listOf(
                activeExercise(
                    id = uuid("20000000-0000-0000-0000-000000000001"),
                    muscleGroups = setOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                ),
                activeExercise(
                    id = uuid("20000000-0000-0000-0000-000000000002"),
                    muscleGroups = setOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
                ),
            )
        )

        assertEquals(
            setOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            draft.targetedMuscleGroups,
        )
    }

    @Test
    fun nonPositiveRestDurationIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            activeWorkout(restDurationSeconds = 0)
        }
    }

    private fun activeWorkout(
        exercises: List<ActiveWorkoutExerciseDraft> = emptyList(),
        restDurationSeconds: Int = 90,
    ) = ActiveWorkoutDraft(
        id = uuid("10000000-0000-0000-0000-000000000001"),
        name = "Empty Workout 2026-09-02",
        sourceTemplateId = null,
        startedAt = Instant.parse("2026-09-02T14:00:00Z"),
        exercises = exercises,
        restDurationSeconds = restDurationSeconds,
    )

    private fun activeExercise(
        id: Uuid = uuid("20000000-0000-0000-0000-000000000001"),
        muscleGroups: Set<MuscleGroup> = setOf(MuscleGroup.CHEST),
    ) = ActiveWorkoutExerciseDraft(
        id = id,
        exerciseId = uuid("550e8400-e29b-41d4-a716-446655440000"),
        exerciseName = "Bench Press",
        positionInWorkout = 0,
        muscleGroups = muscleGroups,
        sets = emptyList(),
    )

    private fun activeSet(
        reps: Int?,
        weight: Double?,
        completed: Boolean,
    ) = ActiveWorkoutSetDraft(
        id = uuid("30000000-0000-0000-0000-000000000001"),
        positionInExercise = 0,
        reps = reps,
        weight = weight,
        weightUnit = WeightUnit.LB,
        completed = completed,
    )

    private fun uuid(value: String): Uuid = Uuid.parse(value)
}
