package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.feature.exercise.di.exerciseModule
import com.example.ogym_kmp.feature.workout.di.workoutModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    baseUrl: String,
    databaseDriverFactory: DatabaseDriverFactory,
    platformConfig: KoinAppDeclaration = {}
) {
    startKoin {
        platformConfig()
        modules(
            coreModule(baseUrl, databaseDriverFactory),
            exerciseModule,
            workoutModule
        )
    }
}