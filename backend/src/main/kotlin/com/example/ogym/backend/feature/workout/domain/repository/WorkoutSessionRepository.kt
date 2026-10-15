package com.example.ogym.backend.feature.workout.domain.repository

import com.example.ogym.backend.feature.workout.domain.WorkoutSession

interface WorkoutSessionRepository {
    fun findAll(): List<WorkoutSession>
    fun findById(id: java.util.UUID): WorkoutSession?
    fun save(workoutSession: WorkoutSession): WorkoutSession
}
