package com.example.ogym.backend.feature.workout.infrastructure

import com.example.ogym.backend.feature.workout.domain.WeightUnit
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("workout_templates")
data class WorkoutTemplateEntity(
    @Id
    val id: UUID,
    @Column("owner_id")
    val ownerId: UUID?,
    val name: String,
    @MappedCollection(
        idColumn = "workout_template_id",
        keyColumn = "position_in_template",
    )
    val exercises: List<TemplateExerciseEntity>,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
)

@Table("template_exercises")
data class TemplateExerciseEntity(
    @Id
    val id: UUID,
    @Column("exercise_id")
    val exerciseId: UUID,
    @MappedCollection(
        idColumn = "template_exercise_id",
        keyColumn = "position_in_exercise",
    )
    val sets: List<TemplateSetEntity>,
)

@Table("template_sets")
data class TemplateSetEntity(
    @Id
    val id: UUID,
    @Column("target_reps")
    val targetReps: Int,
    @Column("target_weight")
    val targetWeight: Double,
    @Column("target_weight_unit")
    val targetWeightUnit: WeightUnit,
)
