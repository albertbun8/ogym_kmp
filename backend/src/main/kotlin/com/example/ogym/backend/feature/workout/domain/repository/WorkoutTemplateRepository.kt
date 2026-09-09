package com.example.ogym.backend.feature.workout.domain.repository

import com.example.ogym.backend.feature.workout.domain.WorkoutTemplate

interface WorkoutTemplateRepository {
    fun findAll(): List<WorkoutTemplate>
    fun findById(id: java.util.UUID): WorkoutTemplate?
    fun save(template: WorkoutTemplate): WorkoutTemplate
}
