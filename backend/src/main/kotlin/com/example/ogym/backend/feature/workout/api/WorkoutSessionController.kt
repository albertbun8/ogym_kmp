package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.application.WorkoutSessionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateWorkoutSessionRequest): WorkoutSessionResponse =
        workoutSessionService.create(request).toResponse()
}
