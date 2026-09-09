package com.example.ogym.backend.feature.workout.application

import com.example.ogym.backend.feature.workout.domain.WorkoutSession
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym.backend.feature.workout.api.CreateWorkoutSessionRequest
import com.example.ogym.backend.feature.workout.domain.WorkoutExercise
import com.example.ogym.backend.feature.workout.domain.WorkoutSet
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class WorkoutSessionService(
    private val workoutSessionsRepository: WorkoutSessionRepository,
) {
    fun getAll(): List<WorkoutSession> {
        return workoutSessionsRepository.findAll()
    }

    fun create(request: CreateWorkoutSessionRequest): WorkoutSession {
        workoutSessionsRepository.findById(request.clientGeneratedId)?.let { return it }
        require(request.name.isNotBlank()) { "Workout name must not be blank." }
        require(request.exercises.isNotEmpty()) { "A completed workout must contain an exercise." }
        require(!request.completedAt.isBefore(request.startedAt)) {
            "Workout completion time must not precede its start time."
        }

        val workoutSession = WorkoutSession(
            id = request.clientGeneratedId,
            ownerId = null,
            name = request.name.trim(),
            sourceTemplateId = request.sourceTemplateId,
            startedAt = request.startedAt,
            completedAt = request.completedAt,
            exercises = request.exercises.map { exercise ->
                WorkoutExercise(
                    id = UUID.randomUUID(),
                    exerciseId = exercise.exerciseId,
                    exerciseName = exercise.exerciseName,
                    positionInWorkout = exercise.positionInWorkout,
                    sets = exercise.sets.map { set ->
                        require(set.reps >= 0) { "Set repetitions must not be negative." }
                        require(set.weight >= 0.0) { "Set weight must not be negative." }
                        WorkoutSet(
                            id = UUID.randomUUID(),
                            positionInExercise = set.positionInExercise,
                            reps = set.reps,
                            weight = set.weight,
                            weightUnit = set.weightUnit,
                            completed = true,
                        )
                    },
                )
            },
            createdAt = Instant.now(),
        )
        return workoutSessionsRepository.save(workoutSession)
    }
}
