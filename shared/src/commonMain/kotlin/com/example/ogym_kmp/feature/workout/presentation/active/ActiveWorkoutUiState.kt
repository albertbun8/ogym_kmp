package com.example.ogym_kmp.feature.workout.presentation.active

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import kotlin.uuid.Uuid
import com.example.ogym_kmp.feature.workout.domain.model.PreviousSetPerformance

data class ActiveWorkoutUiState(
    val workout: ActiveWorkoutDraft,
    val isExercisePickerVisible: Boolean = false,
    val exercisePicker: ExercisePickerState = ExercisePickerState.Idle,
    val setInputs: Map<Uuid, SetInputUi> = emptyMap(),
    val remainingRestSeconds: Int = 0,
    val elapsedWorkoutSeconds: Int = 0,
    val previousPerformances: Map<Uuid, List<PreviousSetPerformance>> = emptyMap(),
    val isFinishing: Boolean = false,
    val finishError: String? = null,
)

data class SetInputUi(
    val weight: String = "",
    val reps: String = "",
)

sealed interface ExercisePickerState {
    data object Idle : ExercisePickerState
    data object Loading : ExercisePickerState
    data class Data(val exercises: List<Exercise>) : ExercisePickerState
    data class Error(val message: String) : ExercisePickerState
}

sealed interface ActiveWorkoutUiAction {
    data object OpenExercisePicker : ActiveWorkoutUiAction
    data object CloseExercisePicker : ActiveWorkoutUiAction
    data object RetryExercises : ActiveWorkoutUiAction
    data class SelectExercise(val exercise: Exercise) : ActiveWorkoutUiAction
    data class AddSet(val exerciseDraftId: Uuid) : ActiveWorkoutUiAction
    data class MoveExercise(val exerciseId: Uuid, val targetExerciseId: Uuid) : ActiveWorkoutUiAction
    data class RemoveExercise(val exerciseId: Uuid) : ActiveWorkoutUiAction
    data class RemoveSet(val setId: Uuid) : ActiveWorkoutUiAction
    data class ChangeSetWeight(val setId: Uuid, val value: String) : ActiveWorkoutUiAction
    data class ChangeSetReps(val setId: Uuid, val value: String) : ActiveWorkoutUiAction
    data class SetSetCompleted(val setId: Uuid, val completed: Boolean) : ActiveWorkoutUiAction
    data object Finish : ActiveWorkoutUiAction
    data object Discard : ActiveWorkoutUiAction
}
