package com.example.ogym.backend.feature.exercise.api

import com.example.ogym.backend.feature.exercise.application.ExerciseService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/exercises")
class ExerciseController(
    private val exerciseService: ExerciseService
) {
    @GetMapping
    fun getAll(): List<ExerciseResponse> {
        return exerciseService
            .getAll()
            .map { exercise -> exercise.toResponse() }
    }
}