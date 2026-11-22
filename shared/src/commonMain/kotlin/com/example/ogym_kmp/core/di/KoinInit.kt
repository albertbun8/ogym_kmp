package com.example.ogym_kmp.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.feature.exercise.di.exerciseModule
import com.example.ogym_kmp.feature.workout.di.workoutModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    baseUrl: String,
    databaseDriverFactory: DatabaseDriverFactory,
    dataStore: DataStore<Preferences>,
    platformConfig: KoinAppDeclaration = {}
) {
    startKoin {
        platformConfig()
        modules(
            coreModule(baseUrl, databaseDriverFactory, dataStore),
            exerciseModule,
            workoutModule
        )
    }
}