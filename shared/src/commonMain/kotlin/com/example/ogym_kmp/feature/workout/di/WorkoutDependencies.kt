package com.example.ogym_kmp.feature.workout.di

import com.example.ogym_kmp.core.database.OGymDatabase
import com.example.ogym_kmp.feature.exercise.domain.repository.ExerciseRepository
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import com.example.ogym_kmp.feature.workout.data.remote.WorkoutApi
import com.example.ogym_kmp.feature.workout.data.repository.SqlDelightActiveWorkoutRepository
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutSessionRepositoryImpl
import com.example.ogym_kmp.feature.workout.data.repository.WorkoutTemplateRepositoryImpl
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
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
import io.ktor.client.HttpClient

class WorkoutDependencies(
    httpClient: HttpClient,
    database: OGymDatabase,
    exerciseRepository: ExerciseRepository,
    private val getExercises: GetExercises
) {
    private val workoutApi = WorkoutApi(httpClient)
    private val workoutTemplateRepository: WorkoutTemplateRepository =
        WorkoutTemplateRepositoryImpl(workoutApi, database)
    private val workoutSessionRepository: WorkoutSessionRepository =
        WorkoutSessionRepositoryImpl(workoutApi, database)
    private val activeWorkoutRepository: ActiveWorkoutRepository =
        SqlDelightActiveWorkoutRepository(database)

    //Use case
    private val getWorkoutTemplates = GetWorkoutTemplates(workoutTemplateRepository)
    private val getWorkoutSessions = GetWorkoutSessions(workoutSessionRepository)
    private val startEmptyWorkout = StartEmptyWorkout(activeWorkoutRepository)
    private val getActiveWorkout = GetActiveWorkout(activeWorkoutRepository)
    private val discardActiveWorkout = DiscardActiveWorkout(activeWorkoutRepository)
    private val addExerciseToActiveWorkout = AddExerciseToActiveWorkout(activeWorkoutRepository)
    private val addSetToActiveWorkout = AddSetToActiveWorkout(activeWorkoutRepository)
    private val reorderActiveWorkoutExercise = ReorderActiveWorkoutExercise(activeWorkoutRepository)
    private val removeExerciseFromActiveWorkout =
        RemoveExerciseFromActiveWorkout(activeWorkoutRepository)
    private val removeSetFromActiveWorkout = RemoveSetFromActiveWorkout(activeWorkoutRepository)
    private val updateActiveWorkoutSet = UpdateActiveWorkoutSet(activeWorkoutRepository)
    private val setActiveWorkoutSetCompletion =
        SetActiveWorkoutSetCompletion(activeWorkoutRepository)
    private val getPreviousExercisePerformances =
        GetPreviousExercisePerformances(workoutSessionRepository)
    private val finishActiveWorkout =
        FinishActiveWorkout(activeWorkoutRepository, workoutSessionRepository)
    private val startWorkoutFromTemplate = StartWorkoutFromTemplate(
        activeWorkoutRepository = activeWorkoutRepository,
        workoutTemplateRepository = workoutTemplateRepository,
        exerciseRepository = exerciseRepository,
    )
    private val createWorkoutTemplate = CreateWorkoutTemplate(workoutTemplateRepository)
    private val updateWorkoutTemplate = UpdateWorkoutTemplate(workoutTemplateRepository)
    private val deleteWorkoutTemplate = DeleteWorkoutTemplate(workoutTemplateRepository)

    //Factory
    fun createHomeViewModel(): HomeViewModel = HomeViewModel(
        getWorkoutTemplates = getWorkoutTemplates,
        getWorkoutSessions = getWorkoutSessions,
        startEmptyWorkout = startEmptyWorkout,
        startWorkoutFromTemplate = startWorkoutFromTemplate,
        getExercises = getExercises,
        deleteWorkoutTemplate = deleteWorkoutTemplate,
    )

    fun createActiveWorkoutSessionViewModel(): ActiveWorkoutSessionViewModel = ActiveWorkoutSessionViewModel(
        getActiveWorkout = getActiveWorkout
    )

    fun createActiveWorkoutViewModel(workout: ActiveWorkoutDraft): ActiveWorkoutViewModel =
        ActiveWorkoutViewModel(
            workout = workout,
            discardActiveWorkout = discardActiveWorkout,
            getExercises = getExercises,
            addExerciseToActiveWorkout = addExerciseToActiveWorkout,
            addSetToActiveWorkout = addSetToActiveWorkout,
            reorderActiveWorkoutExercise = reorderActiveWorkoutExercise,
            removeExerciseFromActiveWorkout = removeExerciseFromActiveWorkout,
            removeSetFromActiveWorkout = removeSetFromActiveWorkout,
            updateActiveWorkoutSet = updateActiveWorkoutSet,
            setActiveWorkoutSetCompletion = setActiveWorkoutSetCompletion,
            getPreviousExercisePerformances = getPreviousExercisePerformances,
            finishActiveWorkout = finishActiveWorkout,
        )

    fun createTemplateEditorViewModel(editedTemplate: WorkoutTemplate?): TemplateEditorViewModel =
        TemplateEditorViewModel(
            getExercises = getExercises,
            createWorkoutTemplate = createWorkoutTemplate,
            updateWorkoutTemplate = updateWorkoutTemplate,
            template = editedTemplate,
        )
}