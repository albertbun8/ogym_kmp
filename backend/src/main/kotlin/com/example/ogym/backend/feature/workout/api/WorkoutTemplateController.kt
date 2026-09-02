package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.application.WorkoutSessionService
import com.example.ogym.backend.feature.workout.application.WorkoutTemplateService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workout-templates")
class WorkoutTemplateController(
    private val workoutTemplateService: WorkoutTemplateService
) {
    @GetMapping
    fun getAll(): List<WorkoutTemplateResponse> {
        return workoutTemplateService
            .getAll()
            .map { workout -> workout.toResponse() }
    }
}