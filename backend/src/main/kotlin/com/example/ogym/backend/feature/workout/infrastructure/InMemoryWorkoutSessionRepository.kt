package com.example.ogym.backend.feature.workout.infrastructure


import com.example.ogym.backend.feature.workout.domain.WorkoutExercise
import com.example.ogym.backend.feature.workout.domain.WorkoutSession
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym.backend.feature.workout.domain.WorkoutSet
import org.springframework.stereotype.Repository
import java.util.UUID
import java.time.Instant

@Repository
class InMemoryWorkoutSessionRepository : WorkoutSessionRepository {

    private val userId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    private val pushTemplateId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")

    private val workoutSessions = listOf(
        WorkoutSession(
            id = UUID.fromString("10000000-0000-0000-0000-000000000001"),
            ownerId = userId,
            name = "Push Day",
            sourceTemplateId = pushTemplateId,
            startedAt = Instant.parse("2026-08-29T18:00:00Z"),
            completedAt = Instant.parse("2026-08-29T19:05:00Z"),
            exercises = listOf(
                WorkoutExercise(
                    id = UUID.fromString("20000000-0000-0000-0000-000000000001"),
                    exerciseId = UUID.fromString(
                        "550e8400-e29b-41d4-a716-446655440000"
                    ),
                    exerciseName = "Bench Press",
                    positionInWorkout = 0,
                    sets = listOf(
                        WorkoutSet(
                            id = UUID.fromString("21000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            reps = 10,
                            weight = 135.0,
                            completed = true
                        ),
                        WorkoutSet(
                            id = UUID.fromString("21000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            reps = 8,
                            weight = 145.0,
                            completed = true
                        ),
                        WorkoutSet(
                            id = UUID.fromString("21000000-0000-0000-0000-000000000003"),
                            positionInExercise = 3,
                            reps = 6,
                            weight = 155.0,
                            completed = true
                        )
                    )
                )
            ),
            createdAt = Instant.parse("2026-08-29T18:00:00Z")
        ),
        WorkoutSession(
            id = UUID.fromString("10000000-0000-0000-0000-000000000002"),
            ownerId = userId,
            name = "Free Workout",
            sourceTemplateId = null,
            startedAt = Instant.parse("2026-08-31T17:30:00Z"),
            completedAt = Instant.parse("2026-08-31T18:10:00Z"),
            exercises = listOf(
                WorkoutExercise(
                    id = UUID.fromString("20000000-0000-0000-0000-000000000002"),
                    exerciseId = UUID.fromString(
                        "6ba7b810-9dad-41d1-80b4-00c04fd430c8"
                    ),
                    exerciseName = "Back Squat",
                    positionInWorkout = 0,
                    sets = listOf(
                        WorkoutSet(
                            id = UUID.fromString("22000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            reps = 8,
                            weight = 185.0,
                            completed = true
                        ),
                        WorkoutSet(
                            id = UUID.fromString("22000000-0000-0000-0000-000000000002"),
                            positionInExercise = 2,
                            reps = 8,
                            weight = 185.0,
                            completed = true
                        )
                    )
                ),
                WorkoutExercise(
                    id = UUID.fromString("20000000-0000-0000-0000-000000000003"),
                    exerciseId = UUID.fromString(
                        "6ba7b811-9dad-41d1-80b4-00c04fd430c8"
                    ),
                    exerciseName = "Pull-Up",
                    positionInWorkout = 1,
                    sets = listOf(
                        WorkoutSet(
                            id = UUID.fromString("23000000-0000-0000-0000-000000000001"),
                            positionInExercise = 1,
                            reps = 10,
                            weight = 0.0,
                            completed = true
                        )
                    )
                )
            ),
            createdAt = Instant.parse("2026-08-31T17:30:00Z")
        )
    )

    override fun findAll(): List<WorkoutSession> = workoutSessions
}
