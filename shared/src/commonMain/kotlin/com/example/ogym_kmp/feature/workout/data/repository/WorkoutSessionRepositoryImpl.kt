package com.example.ogym_kmp.feature.workout.data.repository

import com.example.ogym_kmp.feature.workout.data.mapper.toDomain
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutExerciseRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutSessionRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutSetRequestDto
import kotlin.time.Instant

class WorkoutSessionRepositoryImpl(
    private val workoutApi: WorkoutApi
) : WorkoutSessionRepository {
    override suspend fun getWorkoutSessions(): List<WorkoutSession> {
        return workoutApi.getWorkoutSessions().map { dto ->
            dto.toDomain()
        }
    }

    override suspend fun createWorkoutSession(
        draft: ActiveWorkoutDraft,
        completedAt: Instant,
    ): WorkoutSession {
        val request = CreateWorkoutSessionRequestDto(
            name = draft.name,
            sourceTemplateId = draft.sourceTemplateId?.toString(),
            startedAt = draft.startedAt,
            completedAt = completedAt,
            exercises = draft.exercises.map { exercise ->
                CreateWorkoutExerciseRequestDto(
                    exerciseId = exercise.exerciseId.toString(),
                    exerciseName = exercise.exerciseName,
                    positionInWorkout = exercise.positionInWorkout,
                    sets = exercise.sets.filter { it.completed }.map { set ->
                        CreateWorkoutSetRequestDto(
                            positionInExercise = set.positionInExercise,
                            reps = checkNotNull(set.reps),
                            weight = checkNotNull(set.weight),
                            weightUnit = set.weightUnit,
                        )
                    },
                )
            },
        )
        return workoutApi.createWorkoutSession(request).toDomain()
    }
}
