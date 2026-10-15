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
        ),
        systemExercise("70000000-0000-0000-0000-000000000001", "Deadlift", MuscleGroup.BACK, MuscleGroup.LOWER_BACK, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        systemExercise("70000000-0000-0000-0000-000000000002", "Overhead Press", MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
        systemExercise("70000000-0000-0000-0000-000000000003", "Barbell Row", MuscleGroup.BACK, MuscleGroup.BICEPS),
        systemExercise("70000000-0000-0000-0000-000000000004", "Incline Dumbbell Press", MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
        systemExercise("70000000-0000-0000-0000-000000000005", "Dumbbell Fly", MuscleGroup.CHEST),
        systemExercise("70000000-0000-0000-0000-000000000006", "Push-Up", MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
        systemExercise("70000000-0000-0000-0000-000000000007", "Lat Pulldown", MuscleGroup.BACK, MuscleGroup.BICEPS),
        systemExercise("70000000-0000-0000-0000-000000000008", "Seated Cable Row", MuscleGroup.BACK, MuscleGroup.BICEPS),
        systemExercise("70000000-0000-0000-0000-000000000009", "Face Pull", MuscleGroup.SHOULDERS, MuscleGroup.TRAPEZIUS),
        systemExercise("70000000-0000-0000-0000-000000000010", "Barbell Curl", MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
        systemExercise("70000000-0000-0000-0000-000000000011", "Hammer Curl", MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
        systemExercise("70000000-0000-0000-0000-000000000012", "Triceps Pushdown", MuscleGroup.TRICEPS),
        systemExercise("70000000-0000-0000-0000-000000000013", "Skull Crusher", MuscleGroup.TRICEPS),
        systemExercise("70000000-0000-0000-0000-000000000014", "Lateral Raise", MuscleGroup.SHOULDERS),
        systemExercise("70000000-0000-0000-0000-000000000015", "Barbell Shrug", MuscleGroup.TRAPEZIUS, MuscleGroup.FOREARMS),
        systemExercise("70000000-0000-0000-0000-000000000016", "Romanian Deadlift", MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK),
        systemExercise("70000000-0000-0000-0000-000000000017", "Leg Press", MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
        systemExercise("70000000-0000-0000-0000-000000000018", "Leg Extension", MuscleGroup.QUADRICEPS),
        systemExercise("70000000-0000-0000-0000-000000000019", "Leg Curl", MuscleGroup.HAMSTRINGS),
        systemExercise("70000000-0000-0000-0000-000000000020", "Hip Thrust", MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        systemExercise("70000000-0000-0000-0000-000000000021", "Bulgarian Split Squat", MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        systemExercise("70000000-0000-0000-0000-000000000022", "Standing Calf Raise", MuscleGroup.CALVES),
        systemExercise("70000000-0000-0000-0000-000000000023", "Hip Adduction", MuscleGroup.ADDUCTORS),
        systemExercise("70000000-0000-0000-0000-000000000024", "Hip Abduction", MuscleGroup.ABDUCTORS, MuscleGroup.GLUTES),
        systemExercise("70000000-0000-0000-0000-000000000025", "Plank", MuscleGroup.ABS),
        systemExercise("70000000-0000-0000-0000-000000000026", "Hanging Leg Raise", MuscleGroup.ABS, MuscleGroup.FOREARMS),
        systemExercise("70000000-0000-0000-0000-000000000027", "Back Extension", MuscleGroup.LOWER_BACK, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
        systemExercise("70000000-0000-0000-0000-000000000028", "Farmer's Carry", MuscleGroup.FOREARMS, MuscleGroup.TRAPEZIUS, MuscleGroup.ABS),
    )

    override fun findAll(): List<Exercise> = exercises

    private fun systemExercise(
        id: String,
        name: String,
        vararg muscleGroups: MuscleGroup,
    ) = Exercise(
        id = UUID.fromString(id),
        name = name,
        muscleGroups = muscleGroups.toSet(),
    )
}
