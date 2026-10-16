package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.di.exerciseModule
import com.example.ogym_kmp.feature.workout.di.workoutModule
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import io.ktor.client.HttpClient
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class)
class KoinModulesTest {

    @Test
    fun featureModulesCanBeResolved() {
        val featureModules = module {
            includes(exerciseModule, workoutModule)
        }

        featureModules.verify(
            extraTypes = listOf(
                HttpClient::class,
                OGymDatabase::class,
                ActiveWorkoutDraft::class,
                WorkoutTemplate::class
            ),
        )
    }
}