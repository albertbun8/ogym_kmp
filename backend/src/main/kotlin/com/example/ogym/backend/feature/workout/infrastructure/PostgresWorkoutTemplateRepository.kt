package com.example.ogym.backend.feature.workout.infrastructure

import com.example.ogym.backend.feature.workout.domain.TemplateExercise
import com.example.ogym.backend.feature.workout.domain.TemplateSet
import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate
import com.example.ogym.backend.feature.workout.domain.repository.WorkoutTemplateRepository
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface WorkoutTemplateCrudRepository : CrudRepository<WorkoutTemplateEntity, UUID>

@Repository
class PostgresWorkoutTemplateRepository(
    private val crudRepository: WorkoutTemplateCrudRepository,
    private val aggregateTemplate: JdbcAggregateTemplate,
) : WorkoutTemplateRepository {

    override fun findAll(): List<WorkoutTemplate> =
        crudRepository.findAll().map(WorkoutTemplateEntity::toDomain)

    override fun findById(id: UUID): WorkoutTemplate? =
        crudRepository.findById(id).orElse(null)?.toDomain()

    @Transactional
    override fun save(template: WorkoutTemplate): WorkoutTemplate {
        val entity = template.toEntity()
        val saved = if (crudRepository.existsById(template.id)) {
            crudRepository.save(entity)
        } else {
            aggregateTemplate.insert(entity)
        }
        return saved.toDomain()
    }
}

private fun WorkoutTemplateEntity.toDomain() = WorkoutTemplate(
    id = id,
    ownerId = ownerId,
    name = name,
    exercises = exercises.mapIndexed { exerciseIndex, exercise ->
        TemplateExercise(
            id = exercise.id,
            exerciseId = exercise.exerciseId,
            positionInTemplate = exerciseIndex,
            sets = exercise.sets.mapIndexed { setIndex, set ->
                TemplateSet(
                    id = set.id,
                    positionInExercise = setIndex,
                    targetReps = set.targetReps,
                    targetWeight = set.targetWeight,
                    targetWeightUnit = set.targetWeightUnit,
                )
            },
        )
    },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun WorkoutTemplate.toEntity() = WorkoutTemplateEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    exercises = exercises.sortedBy(TemplateExercise::positionInTemplate).map { exercise ->
        TemplateExerciseEntity(
            id = exercise.id,
            exerciseId = exercise.exerciseId,
            sets = exercise.sets.sortedBy(TemplateSet::positionInExercise).map { set ->
                TemplateSetEntity(
                    id = set.id,
                    targetReps = set.targetReps,
                    targetWeight = set.targetWeight,
                    targetWeightUnit = set.targetWeightUnit,
                )
            },
        )
    },
    createdAt = createdAt,
    updatedAt = updatedAt,
)
