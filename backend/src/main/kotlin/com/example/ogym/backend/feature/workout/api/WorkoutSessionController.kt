package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.application.WorkoutSessionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workout-sessions")
class WorkoutSessionController(
    private val workoutSessionService: WorkoutSessionService
) {
    @GetMapping
    fun getAll(): List<WorkoutSessionResponse> {
        return workoutSessionService
            .getAll()
            .map { workout -> workout.toResponse() }
    }
}