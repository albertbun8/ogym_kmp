package com.example.ogym_kmp.feature.exercise.data.remote

import com.example.ogym_kmp.feature.exercise.data.remote.dto.ExerciseResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ExerciseApi(
    private val httpClient: HttpClient
) {
    suspend fun getExercises(): List<ExerciseResponseDto> {
        return httpClient
            .get("/exercises")
            .body()
    }
}