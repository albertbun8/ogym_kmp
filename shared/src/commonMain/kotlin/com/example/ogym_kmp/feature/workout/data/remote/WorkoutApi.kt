package com.example.ogym_kmp.feature.workout.data.remote

import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutSessionResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutTemplateResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class WorkoutApi(
    private val httpClient: HttpClient
) {
    suspend fun getWorkoutSessions(): List<WorkoutSessionResponseDto> {
        return httpClient
            .get("/workout-sessions")
            .body()
    }

    suspend fun getWorkoutTemplates(): List<WorkoutTemplateResponseDto> {
        return httpClient
            .get("/workout-templates")
            .body()
    }
}