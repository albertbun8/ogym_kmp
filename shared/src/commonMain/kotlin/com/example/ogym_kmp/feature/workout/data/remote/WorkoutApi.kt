package com.example.ogym_kmp.feature.workout.data.remote

import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutSessionResponseDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.WorkoutTemplateResponseDto
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutSessionRequestDto
import com.example.ogym_kmp.feature.workout.data.remote.dto.CreateWorkoutTemplateRequestDto

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

    suspend fun createWorkoutTemplate(request: CreateWorkoutTemplateRequestDto): WorkoutTemplateResponseDto =
        httpClient.post("/workout-templates") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun updateWorkoutTemplate(request: CreateWorkoutTemplateRequestDto): WorkoutTemplateResponseDto =
        httpClient.put("/workout-templates/${request.clientGeneratedId}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun deleteWorkoutTemplate(id: String) {
        httpClient.delete("/workout-templates/$id")
    }

    suspend fun getActiveWorkout(): ActiveWorkoutDraft {
        return httpClient
            .get("/workout-templates")
            .body()
    }
}
