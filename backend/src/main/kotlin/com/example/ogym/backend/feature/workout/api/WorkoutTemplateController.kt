package com.example.ogym.backend.feature.workout.api

import com.example.ogym.backend.feature.workout.application.WorkoutSessionService
import com.example.ogym.backend.feature.workout.application.WorkoutTemplateService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateWorkoutTemplateRequest): WorkoutTemplateResponse =
        workoutTemplateService.create(request).toResponse()

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: java.util.UUID,
        @RequestBody request: CreateWorkoutTemplateRequest,
    ): WorkoutTemplateResponse = workoutTemplateService.update(id, request).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: java.util.UUID) = workoutTemplateService.delete(id)
}
