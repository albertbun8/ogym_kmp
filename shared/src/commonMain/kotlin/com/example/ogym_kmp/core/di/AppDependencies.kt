package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.network.createHttpClient
import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.data.repository.InMemoryActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutSessionRepositoryImpl
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutTemplateRepositoryImpl
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import com.example.ogym_kmp.feature.workout.domain.usecase.DiscardActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.AddExerciseToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.AddSetToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.GetActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutSessions
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutTemplates
import com.example.ogym_kmp.feature.workout.domain.usecase.GetPreviousExercisePerformances
import com.example.ogym_kmp.feature.workout.domain.usecase.FinishActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.StartEmptyWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.SetActiveWorkoutSetCompletion
import com.example.ogym_kmp.feature.workout.domain.usecase.UpdateActiveWorkoutSet

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
    private val activeWorkoutRepository: ActiveWorkoutRepository = InMemoryActiveWorkoutRepository()

    val getWorkoutTemplates = GetWorkoutTemplates(workoutTemplateRepository)
    val getWorkoutSessions = GetWorkoutSessions(workoutSessionRepository)
    val startEmptyWorkout = StartEmptyWorkout(activeWorkoutRepository)
    val getActiveWorkout = GetActiveWorkout(activeWorkoutRepository)
    val discardActiveWorkout = DiscardActiveWorkout(activeWorkoutRepository)
    val addExerciseToActiveWorkout = AddExerciseToActiveWorkout(activeWorkoutRepository)
    val addSetToActiveWorkout = AddSetToActiveWorkout(activeWorkoutRepository)
    val updateActiveWorkoutSet = UpdateActiveWorkoutSet(activeWorkoutRepository)
    val setActiveWorkoutSetCompletion = SetActiveWorkoutSetCompletion(activeWorkoutRepository)
    val getPreviousExercisePerformances = GetPreviousExercisePerformances(workoutSessionRepository)
    val finishActiveWorkout = FinishActiveWorkout(activeWorkoutRepository, workoutSessionRepository)
}
