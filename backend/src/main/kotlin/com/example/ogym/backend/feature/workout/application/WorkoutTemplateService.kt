package com.example.ogym.backend.feature.workout.application

import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutTemplateRepository
import org.springframework.stereotype.Service
import com.example.ogym.backend.feature.workout.api.CreateWorkoutTemplateRequest
import com.example.ogym.backend.feature.workout.domain.TemplateExercise
import com.example.ogym.backend.feature.workout.domain.TemplateSet
import java.time.Instant
import java.util.UUID

@Service
class WorkoutTemplateService(
    private val workoutTemplateRepository: WorkoutTemplateRepository
) {
    fun getAll(): List<WorkoutTemplate> {
        return workoutTemplateRepository.findAll()
    }

    fun create(request: CreateWorkoutTemplateRequest): WorkoutTemplate {
        workoutTemplateRepository.findById(request.clientGeneratedId)?.let { return it }
        require(request.name.isNotBlank()) { "Template name must not be blank." }
        require(request.exercises.isNotEmpty()) { "A template must contain at least one exercise." }
        val now = Instant.now()
        return workoutTemplateRepository.save(
            WorkoutTemplate(
                id = request.clientGeneratedId,
                ownerId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                name = request.name.trim(),
                exercises = request.exercises.map { exercise ->
                    TemplateExercise(
                        id = UUID.randomUUID(), exerciseId = exercise.exerciseId,
                        positionInTemplate = exercise.positionInTemplate,
                        sets = exercise.sets.map { set ->
                            require(set.targetReps >= 0 && set.targetWeight >= 0.0)
                            TemplateSet(
                                UUID.randomUUID(), set.positionInExercise, set.targetReps,
                                set.targetWeight, set.targetWeightUnit,
                            )
                        },
                    )
                },
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    fun update(id: UUID, request: CreateWorkoutTemplateRequest): WorkoutTemplate {
        require(id == request.clientGeneratedId) { "Template id does not match the request path." }
        val existing = requireNotNull(workoutTemplateRepository.findById(id)) { "Template not found." }
        require(request.name.isNotBlank()) { "Template name must not be blank." }
        require(request.exercises.isNotEmpty()) { "A template must contain at least one exercise." }
        return workoutTemplateRepository.save(
            existing.copy(
                name = request.name.trim(),
                exercises = request.exercises.map { exercise ->
                    TemplateExercise(
                        id = UUID.randomUUID(),
                        exerciseId = exercise.exerciseId,
                        positionInTemplate = exercise.positionInTemplate,
                        sets = exercise.sets.map { set ->
                            require(set.targetReps >= 0 && set.targetWeight >= 0.0)
                            TemplateSet(
                                id = UUID.randomUUID(),
                                positionInExercise = set.positionInExercise,
                                targetReps = set.targetReps,
                                targetWeight = set.targetWeight,
                                targetWeightUnit = set.targetWeightUnit,
                            )
                        },
                    )
                },
                updatedAt = Instant.now(),
            ),
        )
    }

    fun delete(id: UUID) {
        requireNotNull(workoutTemplateRepository.findById(id)) { "Template not found." }
        workoutTemplateRepository.deleteById(id)
    }
}
