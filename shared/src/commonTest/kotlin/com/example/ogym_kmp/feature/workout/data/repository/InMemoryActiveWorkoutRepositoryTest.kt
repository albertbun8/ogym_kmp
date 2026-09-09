package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

class InMemoryActiveWorkoutRepositoryTest {

    @Test
    fun initiallyReturnsNoActiveWorkout() = runTest {
        val repository = InMemoryActiveWorkoutRepository()

        assertNull(repository.getActiveWorkout())
    }

    @Test
    fun savedWorkoutCanBeRestored() = runTest {
        val repository = InMemoryActiveWorkoutRepository()
        val draft = activeWorkout(
            id = "10000000-0000-0000-0000-000000000001",
            name = "Empty Workout 2026-09-07",
        )

        repository.saveActiveWorkout(draft)

        assertEquals(draft, repository.getActiveWorkout())
    }

    @Test
    fun savingAnotherWorkoutReplacesTheCurrentWorkout() = runTest {
        val repository = InMemoryActiveWorkoutRepository()
        val firstDraft = activeWorkout(
            id = "10000000-0000-0000-0000-000000000001",
            name = "First Workout",
        )
        val secondDraft = activeWorkout(
            id = "10000000-0000-0000-0000-000000000002",
            name = "Second Workout",
        )

        repository.saveActiveWorkout(firstDraft)
        repository.saveActiveWorkout(secondDraft)

        assertEquals(secondDraft, repository.getActiveWorkout())
    }

    @Test
    fun clearingRemovesTheCurrentWorkout() = runTest {
        val repository = InMemoryActiveWorkoutRepository()
        repository.saveActiveWorkout(
            activeWorkout(
                id = "10000000-0000-0000-0000-000000000001",
                name = "Empty Workout 2026-09-07",
            )
        )

        repository.clearActiveWorkout()

        assertNull(repository.getActiveWorkout())
    }

    private fun activeWorkout(
        id: String,
        name: String,
    ) = ActiveWorkoutDraft(
        id = Uuid.parse(id),
        name = name,
        sourceTemplateId = null,
        startedAt = Instant.parse("2026-09-07T14:00:00Z"),
        exercises = emptyList(),
    )
}
