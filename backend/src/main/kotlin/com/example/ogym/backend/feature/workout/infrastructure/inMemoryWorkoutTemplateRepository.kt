package com.example.ogym.backend.feature.workout.infrastructure

import com.example.ogym.backend.feature.workout.domain.TemplateExercise
import com.example.ogym.backend.feature.workout.domain.TemplateSet
import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutTemplateRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.time.Instant

@Repository
class InMemoryWorkoutTemplateRepository : WorkoutTemplateRepository {
    private val userId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")

    private val workoutTemplates = listOf(
        WorkoutTemplate(
            id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            ownerId = userId,
            name = "Push Day",
            exercises = listOf(
                TemplateExercise(
                    id = UUID.fromString("30000000-0000-0000-0000-000000000001"),
                    exerciseId = UUID.fromString(
                        "550e8400-e29b-41d4-a716-446655440000"
                    ),
                    positionInTemplate = 0,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("31000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            targetReps = 10,
                            targetWeight = 135.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("31000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            targetReps = 8,
                            targetWeight = 145.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("31000000-0000-0000-0000-000000000003"),
                            positionInExercise = 3,
                            targetReps = 6,
                            targetWeight = 155.0
                        )
                    )
                ),
                TemplateExercise(
                    id = UUID.fromString("30000000-0000-0000-0000-000000000002"),
                    exerciseId = UUID.fromString(
                        "6ba7b811-9dad-41d1-80b4-00c04fd430c8"
                    ),
                    positionInTemplate = 1,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("32000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            targetReps = 10,
                            targetWeight = 0.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("32000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            targetReps = 8,
                            targetWeight = 0.0
                        )
                    )
                )
            ),
            createdAt = Instant.parse("2026-08-20T14:00:00Z"),
            updatedAt = Instant.parse("2026-08-25T16:30:00Z")
        ),
        WorkoutTemplate(
            id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
            ownerId = null,
            name = "OGym Beginner Full Body",
            exercises = listOf(
                TemplateExercise(
                    id = UUID.fromString("30000000-0000-0000-0000-000000000003"),
                    exerciseId = UUID.fromString(
                        "6ba7b810-9dad-41d1-80b4-00c04fd430c8"
                    ),
                    positionInTemplate = 0,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("31000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            targetReps = 10,
                            targetWeight = 95.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("33000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            targetReps = 10,
                            targetWeight = 95.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("33000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            targetReps = 10,
                            targetWeight = 95.0
                        )
                    )
                ),
                TemplateExercise(
                    id = UUID.fromString("30000000-0000-0000-0000-000000000004"),
                    exerciseId = UUID.fromString(
                        "550e8400-e29b-41d4-a716-446655440000"
                    ),
                    positionInTemplate = 1,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("34000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            targetReps = 10,
                            targetWeight = 65.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("34000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            targetReps = 10,
                            targetWeight = 65.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("34000000-0000-0000-0000-000000000003"),
                            positionInExercise = 3,
                            targetReps = 10,
                            targetWeight = 65.0
                        )
                    )
                ),
                TemplateExercise(
                    id = UUID.fromString("30000000-0000-0000-0000-000000000005"),
                    exerciseId = UUID.fromString(
                        "6ba7b811-9dad-41d1-80b4-00c04fd430c8"
                    ),
                    positionInTemplate = 2,
                    sets = listOf(
                        TemplateSet(
                            id = UUID.fromString("35000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            targetReps = 5,
                            targetWeight = 0.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("35000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            targetReps = 5,
                            targetWeight = 0.0
                        ),
                        TemplateSet(
                            id = UUID.fromString("35000000-0000-0000-0000-000000000003"),
                            positionInExercise = 3,
                            targetReps = 5,
                            targetWeight = 0.0
                        )
                    )
                )
            ),
            createdAt = Instant.parse("2026-08-01T12:00:00Z"),
            updatedAt = Instant.parse("2026-08-01T12:00:00Z")
        )
    )

    override fun findAll(): List<WorkoutTemplate> = workoutTemplates
}
