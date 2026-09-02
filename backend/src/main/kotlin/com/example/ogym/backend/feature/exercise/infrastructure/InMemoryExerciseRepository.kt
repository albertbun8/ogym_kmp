package com.example.ogym.backend.feature.exercise.infrastructure

import com.example.ogym.backend.feature.exercise.domain.Exercise
import com.example.ogym.backend.feature.exercise.domain.ExerciseRepository
import com.example.ogym.backend.feature.exercise.domain.MuscleGroup
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryExerciseRepository : ExerciseRepository {

    private val exercises = listOf(
        Exercise(
            id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
            name = "Bench Press",
            muscleGroups = setOf(
                MuscleGroup.CHEST,
                MuscleGroup.TRICEPS,
                MuscleGroup.SHOULDERS
            )
        ),
        Exercise(
            id = UUID.fromString("6ba7b810-9dad-41d1-80b4-00c04fd430c8"),
            name = "Back Squat",
            muscleGroups = setOf(
                MuscleGroup.QUADRICEPS,
                MuscleGroup.GLUTES,
                MuscleGroup.HAMSTRINGS
            )
        ),
        Exercise(
            id = UUID.fromString("6ba7b811-9dad-41d1-80b4-00c04fd430c8"),
            name = "Pull-Up",
            muscleGroups = setOf(
                MuscleGroup.BACK,
                MuscleGroup.BICEPS
            )
        )
    )

    override fun findAll(): List<Exercise> = exercises
}