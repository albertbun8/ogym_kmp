package com.example.ogym_kmp.feature.exercise.di

import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import io.ktor.client.HttpClient

class ExerciseDependencies(
    httpClient: HttpClient,
    database: OGymDatabase,
) {
    private val exerciseApi = ExerciseApi(httpClient)
    val exerciseRepository: ExerciseRepository = ExerciseRepositoryImpl(exerciseApi, database)
    val getExercises = GetExercises(exerciseRepository)
}