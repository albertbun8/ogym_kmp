package com.example.ogym_kmp.core.di

import com.example.ogym_kmp.core.network.createHttpClient
import com.example.ogym_kmp.core.database.DatabaseDriverFactory
import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.data.remote.ExerciseApi
import com.example.ogym_kmp.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.data.repository.SqlDelightActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutSessionRepositoryImpl
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutTemplateRepositoryImpl
import com.example.ogym_kmp.feature.workout.domain.repository.ActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutSessionRepository
import com.example.ogym_kmp.feature.workout.domain.repository.WorkoutTemplateRepository
import com.example.ogym_kmp.feature.workout.domain.usecase.DiscardActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.AddExerciseToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.AddSetToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.ReorderActiveWorkoutExercise
import com.example.ogym_kmp.feature.workout.domain.usecase.RemoveExerciseFromActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.RemoveSetFromActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.GetActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutSessions
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutTemplates
import com.example.ogym_kmp.feature.workout.domain.usecase.GetPreviousExercisePerformances
import com.example.ogym_kmp.feature.workout.domain.usecase.FinishActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.CreateWorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.StartEmptyWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.StartWorkoutFromTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.SetActiveWorkoutSetCompletion
import com.example.ogym_kmp.feature.workout.domain.usecase.UpdateActiveWorkoutSet

class AppDependencies(
    baseUrl: String,
    databaseDriverFactory: DatabaseDriverFactory,
) {
    private val httpClient = createHttpClient(baseUrl)

    private val exerciseApi = ExerciseApi(httpClient)
    private val database = OGymDatabase(databaseDriverFactory.createDriver())
    private val exerciseRepository: ExerciseRepository = ExerciseRepositoryImpl(exerciseApi, database)
    val getExercises = GetExercises(exerciseRepository)

    private val workoutApi = WorkoutApi(httpClient)
    private val workoutTemplateRepository: WorkoutTemplateRepository = WorkoutTemplateRepositoryImpl(workoutApi, database)
    private val workoutSessionRepository: WorkoutSessionRepository = WorkoutSessionRepositoryImpl(workoutApi, database)
    private val activeWorkoutRepository: ActiveWorkoutRepository =
        SqlDelightActiveWorkoutRepository(database)

    val getWorkoutTemplates = GetWorkoutTemplates(workoutTemplateRepository)
    val getWorkoutSessions = GetWorkoutSessions(workoutSessionRepository)
    val startEmptyWorkout = StartEmptyWorkout(activeWorkoutRepository)
    val getActiveWorkout = GetActiveWorkout(activeWorkoutRepository)
    val discardActiveWorkout = DiscardActiveWorkout(activeWorkoutRepository)
    val addExerciseToActiveWorkout = AddExerciseToActiveWorkout(activeWorkoutRepository)
    val addSetToActiveWorkout = AddSetToActiveWorkout(activeWorkoutRepository)
    val reorderActiveWorkoutExercise = ReorderActiveWorkoutExercise(activeWorkoutRepository)
    val removeExerciseFromActiveWorkout = RemoveExerciseFromActiveWorkout(activeWorkoutRepository)
    val removeSetFromActiveWorkout = RemoveSetFromActiveWorkout(activeWorkoutRepository)
    val updateActiveWorkoutSet = UpdateActiveWorkoutSet(activeWorkoutRepository)
    val setActiveWorkoutSetCompletion = SetActiveWorkoutSetCompletion(activeWorkoutRepository)
    val getPreviousExercisePerformances = GetPreviousExercisePerformances(workoutSessionRepository)
    val finishActiveWorkout = FinishActiveWorkout(activeWorkoutRepository, workoutSessionRepository)
    val startWorkoutFromTemplate = StartWorkoutFromTemplate(
        activeWorkoutRepository = activeWorkoutRepository,
        workoutTemplateRepository = workoutTemplateRepository,
        exerciseRepository = exerciseRepository,
    )
    val createWorkoutTemplate = CreateWorkoutTemplate(workoutTemplateRepository)
}
