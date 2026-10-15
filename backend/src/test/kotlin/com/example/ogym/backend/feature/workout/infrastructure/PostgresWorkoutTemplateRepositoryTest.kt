package com.example.ogym.backend.feature.workout.infrastructure

import com.example.ogym.backend.feature.workout.domain.TemplateExercise
import com.example.ogym.backend.feature.workout.domain.TemplateSet
import com.example.ogym.backend.feature.workout.domain.WeightUnit
import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutTemplateRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.time.Instant
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class PostgresWorkoutTemplateRepositoryTest {

    @Autowired
    private lateinit var repository: WorkoutTemplateRepository

    @Test
    fun `loads the seeded aggregate with exercises and sets`() {
        val template = requireNotNull(repository.findById(
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
        ))

        assertEquals("Push Day", template.name)
        assertEquals(2, template.exercises.size)
        assertEquals(3, template.exercises.first().sets.size)
        assertEquals(0, template.exercises.first().positionInTemplate)
        assertEquals(0, template.exercises.first().sets.first().positionInExercise)
    }

    @Test
    fun `inserts a client generated aggregate and reads it back`() {
        val templateId = UUID.fromString("90000000-0000-0000-0000-000000000001")
        val template = WorkoutTemplate(
            id = templateId,
            ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            name = "Integration Test Template",
            exercises = listOf(
                TemplateExercise(
                    id = UUID.fromString("90000000-0000-0000-0000-000000000002"),
                    exerciseId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                    positionInTemplate = 0,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("90000000-0000-0000-0000-000000000003"),
                            positionInExercise = 0,
                            targetReps = 8,
                            targetWeight = 82.5,
                            targetWeightUnit = WeightUnit.KG,
                        ),
                    ),
                ),
            ),
            createdAt = Instant.parse("2026-09-08T12:00:00Z"),
            updatedAt = Instant.parse("2026-09-08T12:00:00Z"),
        )

        repository.save(template)

        assertEquals(template, repository.findById(templateId))
    }

    @Test
    fun `updates an aggregate and replaces its children`() {
        val templateId = UUID.fromString("90000000-0000-0000-0000-000000000011")
        val original = WorkoutTemplate(
            id = templateId,
            ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            name = "Original",
            exercises = listOf(
                TemplateExercise(
                    id = UUID.fromString("90000000-0000-0000-0000-000000000012"),
                    exerciseId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                    positionInTemplate = 0,
                    sets = emptyList(),
                ),
            ),
            createdAt = Instant.parse("2026-09-08T12:00:00Z"),
            updatedAt = Instant.parse("2026-09-08T12:00:00Z"),
        )
        repository.save(original)

        val updated = original.copy(
            name = "Updated",
            exercises = listOf(
                TemplateExercise(
                    id = UUID.fromString("90000000-0000-0000-0000-000000000013"),
                    exerciseId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                    positionInTemplate = 0,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("90000000-0000-0000-0000-000000000014"),
                            positionInExercise = 0,
                            targetReps = 12,
                            targetWeight = 45.0,
                            targetWeightUnit = WeightUnit.LB,
                        ),
                    ),
                ),
            ),
            updatedAt = Instant.parse("2026-09-08T13:00:00Z"),
        )

        repository.save(updated)

        assertEquals(updated, repository.findById(templateId))
    }

    companion object {
        @Container
        @JvmField
        val postgres = PostgreSQLContainer("postgres:17-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun databaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
