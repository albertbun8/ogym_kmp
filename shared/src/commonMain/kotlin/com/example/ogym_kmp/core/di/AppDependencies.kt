package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.network.createHttpClient
import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutSessionRepositoryImpl
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutTemplateRepositoryImpl
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutSessions
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutTemplates

class AppDependencies(
    baseUrl: String
) {
    private val httpClient = createHttpClient(baseUrl)

    private val exerciseApi = ExerciseApi(httpClient)
    private val exerciseRepository: ExerciseRepository = ExerciseRepositoryImpl(exerciseApi)
    val getExercises = GetExercises(exerciseRepository)

    private val workoutApi = WorkoutApi(httpClient)
    private val workoutTemplateRepository: WorkoutTemplateRepository = WorkoutTemplateRepositoryImpl(workoutApi)
    private val workoutSessionRepository: WorkoutSessionRepository = WorkoutSessionRepositoryImpl(workoutApi)

    val getWorkoutTemplates = GetWorkoutTemplates(workoutTemplateRepository)
    val getWorkoutSessions = GetWorkoutSessions(workoutSessionRepository)
}
