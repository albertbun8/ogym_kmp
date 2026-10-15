package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.network.createHttpClient
import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.di.ExerciseDependencies
import com.example.ogym_kmp.feature.workout.di.WorkoutDependencies

class AppDependencies(
    baseUrl: String,
    databaseDriverFactory: DatabaseDriverFactory,
) {
    private val httpClient = createHttpClient(baseUrl)
    private val database = OGymDatabase(databaseDriverFactory.createDriver())

    val exercise = ExerciseDependencies(
        httpClient = httpClient,
        database = database
    )

    val workout = WorkoutDependencies(
        httpClient = httpClient,
        database = database,
        exerciseRepository = exercise.exerciseRepository,
        getExercises = exercise.getExercises,
    )
}
