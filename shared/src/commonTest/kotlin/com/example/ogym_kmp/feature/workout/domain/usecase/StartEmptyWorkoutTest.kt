package com.example.ogym_kmp.feature.workout.domain.usecase

import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.core.preferences.FakeWeightUnitPreferences
import com.example.ogym_kmp.feature.workout.data.repository.InMemoryActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class StartEmptyWorkoutTest {
    @Test
    fun newWorkoutUsesPreferredWeightUnit() = runTest {
        // Given: a preference that is NOT the default (LB),
        // so the test fails if the use case ignores the preference.
        val startEmptyWorkout = StartEmptyWorkout(
            activeWorkoutRepository = InMemoryActiveWorkoutRepository(), // empty: no active workout
            weightUnitPreferences = FakeWeightUnitPreferences(WeightUnit.KG),
        )

        // When: we start a workout. Fixed values (no Uuid.random() / current time)
        // keep the test deterministic: same result on every run.
        val draft = startEmptyWorkout(
            id = Uuid.parse("10000000-0000-0000-0000-000000000001"),
            startedAt = Instant.parse("2026-09-07T14:00:00Z"),
            localDate = "2026-09-07",
        )

        // Then: we check ONE behavior: the unit. Not the name or the id,
        // which would be other tests if they mattered.
        assertEquals(WeightUnit.KG, draft.preferredWeightUnit)
    }

    @Test
    fun existingWorkoutKeepsItsWeightUnit() = runTest {
        // Given: an active workout already exists, in LB...
        val existingDraft = ActiveWorkoutDraft(
            id = Uuid.parse("10000000-0000-0000-0000-000000000002"),
            name = "Existing Workout",
            sourceTemplateId = null,
            startedAt = Instant.parse("2026-09-07T13:00:00Z"),
            exercises = emptyList(),
            preferredWeightUnit = WeightUnit.LB,
        )
        val repository = InMemoryActiveWorkoutRepository()
        repository.saveActiveWorkout(existingDraft)
        // ...and the preference is the OPPOSITE unit, so the test can tell
        // "kept the existing draft" apart from "used the preference".
        val startEmptyWorkout = StartEmptyWorkout(
            activeWorkoutRepository = repository,
            weightUnitPreferences = FakeWeightUnitPreferences(WeightUnit.KG),
        )

        // When: we try to start a new workout (different id than the existing one).
        val draft = startEmptyWorkout(
            id = Uuid.parse("10000000-0000-0000-0000-000000000001"),
            startedAt = Instant.parse("2026-09-07T14:00:00Z"),
            localDate = "2026-09-07",
        )

        // Then: the existing draft is returned unchanged (data class equality
        // compares every field: id, name, unit...).
        assertEquals(existingDraft, draft)
    }
}