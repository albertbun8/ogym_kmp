package com.example.ogym.backend.feature.workout.application

import com.example.ogym.backend.feature.workout.api.CreateTemplateExerciseRequest
import com.example.ogym.backend.feature.workout.api.CreateTemplateSetRequest
import com.example.ogym.backend.feature.workout.api.CreateWorkoutTemplateRequest
import com.example.ogym.backend.feature.workout.domain.TemplateExercise
import com.example.ogym.backend.feature.workout.domain.WeightUnit
import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutTemplateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class WorkoutTemplateServiceTest {
    @Test
    fun `update preserves template identity and replaces editable content`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val createdAt = Instant.parse("2026-09-08T12:00:00Z")
        val repository = FakeWorkoutTemplateRepository(
            WorkoutTemplate(
                id = id,
                ownerId = ownerId,
                name = "Original",
                exercises = listOf(
                    TemplateExercise(UUID.randomUUID(), UUID.randomUUID(), 0, emptyList()),
                ),
                createdAt = createdAt,
                updatedAt = createdAt,
            ),
        )
        val exerciseId = UUID.randomUUID()

        val updated = WorkoutTemplateService(repository).update(
            id,
            CreateWorkoutTemplateRequest(
                clientGeneratedId = id,
                name = " Updated ",
                exercises = listOf(
                    CreateTemplateExerciseRequest(
                        exerciseId = exerciseId,
                        positionInTemplate = 0,
                        sets = listOf(CreateTemplateSetRequest(0, 12, 45.0, WeightUnit.LB)),
                    ),
                ),
            ),
        )

        assertEquals(id, updated.id)
        assertEquals(ownerId, updated.ownerId)
        assertEquals(createdAt, updated.createdAt)
        assertEquals("Updated", updated.name)
        assertEquals(exerciseId, updated.exercises.single().exerciseId)
        assertEquals(12, updated.exercises.single().sets.single().targetReps)
        assertNotEquals(createdAt, updated.updatedAt)
    }
}

private class FakeWorkoutTemplateRepository(initial: WorkoutTemplate) : WorkoutTemplateRepository {
    private var template = initial

    override fun findAll(): List<WorkoutTemplate> = listOf(template)
    override fun findById(id: UUID): WorkoutTemplate? = template.takeIf { it.id == id }
    override fun save(template: WorkoutTemplate): WorkoutTemplate = template.also { this.template = it }
    override fun deleteById(id: UUID) {
        require(template.id == id)
    }
}
