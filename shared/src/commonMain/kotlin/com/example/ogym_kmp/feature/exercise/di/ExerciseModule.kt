package com.example.ogym_kmp.feature.exercise.di

import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import org.koin.dsl.module

val exerciseModule = module {
    single { ExerciseApi(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get(), get()) }
    factory { GetExercises(get()) }
}