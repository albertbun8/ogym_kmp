package com.example.ogym_kmp.feature.workout.data.remote

import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutSessionResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutTemplateResponseDto
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutSessionRequestDto

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

    suspend fun createWorkoutSession(
        request: CreateWorkoutSessionRequestDto,
    ): WorkoutSessionResponseDto = httpClient
        .post("/workout-sessions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        .body()

    suspend fun getActiveWorkout(): ActiveWorkoutDraft {
        return httpClient
            .get("/workout-templates")
            .body()
    }
}
