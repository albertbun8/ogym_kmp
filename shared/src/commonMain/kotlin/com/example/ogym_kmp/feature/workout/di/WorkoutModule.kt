package com.example.ogym_kmp.feature.workout.di

import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.data.repository.SqlDelightActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutSessionRepositoryImpl
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutTemplateRepositoryImpl
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import com.example.ogym_kmp.feature.workout.domain.usecase.AddExerciseToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.AddSetToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.CreateWorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.DeleteWorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.DiscardActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.FinishActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.GetActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.GetPreviousExercisePerformances
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutSessions
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutTemplates
import com.example.ogym_kmp.feature.workout.domain.usecase.RemoveExerciseFromActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.RemoveSetFromActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.ReorderActiveWorkoutExercise
import com.example.ogym_kmp.feature.workout.domain.usecase.SetActiveWorkoutSetCompletion
import com.example.ogym_kmp.feature.workout.domain.usecase.StartEmptyWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.StartWorkoutFromTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.UpdateActiveWorkoutSet
import com.example.ogym_kmp.feature.workout.domain.usecase.UpdateWorkoutTemplate
import com.example.ogym_kmp.feature.workout.presentation.active.ActiveWorkoutViewModel
import com.example.ogym_kmp.feature.workout.presentation.home.HomeViewModel
import com.example.ogym_kmp.feature.workout.presentation.session.ActiveWorkoutSessionViewModel
import com.example.ogym_kmp.feature.workout.presentation.template.TemplateEditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val workoutModule = module {
    single { WorkoutApi(get()) }
    single<WorkoutTemplateRepository> { WorkoutTemplateRepositoryImpl(get(), get()) }
    single<WorkoutSessionRepository> { WorkoutSessionRepositoryImpl(get(), get()) }
    single<ActiveWorkoutRepository> { SqlDelightActiveWorkoutRepository(get()) }

    factory { GetWorkoutTemplates(get()) }
    factory { GetWorkoutSessions(get()) }
    factory { StartEmptyWorkout(get()) }
    factory { GetActiveWorkout(get()) }
    factory { DiscardActiveWorkout(get()) }
    factory { AddExerciseToActiveWorkout(get()) }
    factory { AddSetToActiveWorkout(get()) }
    factory { ReorderActiveWorkoutExercise(get()) }
    factory { RemoveExerciseFromActiveWorkout(get()) }
    factory { RemoveSetFromActiveWorkout(get()) }
    factory { UpdateActiveWorkoutSet(get()) }
    factory { SetActiveWorkoutSetCompletion(get()) }
    factory { GetPreviousExercisePerformances(get()) }
    factory { FinishActiveWorkout(get(), get()) }
    factory { StartWorkoutFromTemplate(get(), get(), get()) }
    factory { CreateWorkoutTemplate(get()) }
    factory { UpdateWorkoutTemplate(get()) }
    factory { DeleteWorkoutTemplate(get()) }

    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ActiveWorkoutSessionViewModel(get()) }
    viewModel { params ->
        ActiveWorkoutViewModel(
            workout = params.get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { params ->
        TemplateEditorViewModel(
            get(),
            get(),
            get(),
            template = params.getOrNull()
        )
    }
}