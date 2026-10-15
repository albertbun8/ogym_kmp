package com.example.ogym_kmp.feature.workout.presentation.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.usecase.DiscardActiveWorkout
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import com.example.ogym_kmp.feature.workout.domain.usecase.AddExerciseToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.AddSetToActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.ReorderActiveWorkoutExercise
import com.example.ogym_kmp.feature.workout.domain.usecase.RemoveExerciseFromActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.RemoveSetFromActiveWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.UpdateActiveWorkoutSet
import com.example.ogym_kmp.feature.workout.domain.usecase.SetActiveWorkoutSetCompletion
import com.example.ogym_kmp.feature.workout.domain.usecase.GetPreviousExercisePerformances
import com.example.ogym_kmp.feature.workout.domain.usecase.FinishActiveWorkout
import com.example.ogym_kmp.core.common.currentInstant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ActiveWorkoutViewModel(
    workout: ActiveWorkoutDraft,
    private val discardActiveWorkout: DiscardActiveWorkout,
    private val getExercises: GetExercises,
    private val addExerciseToActiveWorkout: AddExerciseToActiveWorkout,
    private val addSetToActiveWorkout: AddSetToActiveWorkout,
    private val reorderActiveWorkoutExercise: ReorderActiveWorkoutExercise,
    private val removeExerciseFromActiveWorkout: RemoveExerciseFromActiveWorkout,
    private val removeSetFromActiveWorkout: RemoveSetFromActiveWorkout,
    private val updateActiveWorkoutSet: UpdateActiveWorkoutSet,
    private val setActiveWorkoutSetCompletion: SetActiveWorkoutSetCompletion,
    private val getPreviousExercisePerformances: GetPreviousExercisePerformances,
    private val finishActiveWorkout: FinishActiveWorkout,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ActiveWorkoutUiState(
            workout = workout,
            setInputs = workout.exercises
                .flatMap { it.sets }
                .associate { set ->
                    set.id to SetInputUi(
                        weight = set.weight?.toInputString().orEmpty(),
                        reps = set.reps?.toString().orEmpty(),
                    )
                },
        ),
    )
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private val _events = Channel<ActiveWorkoutEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    private var restTimerJob: Job? = null
    private var workoutTimerJob: Job? = null

    init {
        startRestTimer()
        startWorkoutTimer()
        loadPreviousPerformances()
    }

    fun onAction(action: ActiveWorkoutUiAction) {
        when (action) {
            ActiveWorkoutUiAction.OpenExercisePicker -> openExercisePicker()
            ActiveWorkoutUiAction.CloseExercisePicker -> closeExercisePicker()
            ActiveWorkoutUiAction.RetryExercises -> loadExercises()
            is ActiveWorkoutUiAction.SelectExercise -> addExercise(action.exercise)
            is ActiveWorkoutUiAction.AddSet -> addSet(action.exerciseDraftId)
            is ActiveWorkoutUiAction.MoveExercise -> moveExercise(action.exerciseId, action.targetExerciseId)
            is ActiveWorkoutUiAction.RemoveExercise -> removeExercise(action.exerciseId)
            is ActiveWorkoutUiAction.RemoveSet -> removeSet(action.setId)
            is ActiveWorkoutUiAction.ChangeSetWeight -> changeSetWeight(action.setId, action.value)
            is ActiveWorkoutUiAction.ChangeSetReps -> changeSetReps(action.setId, action.value)
            is ActiveWorkoutUiAction.SetSetCompleted -> setCompleted(action.setId, action.completed)
            ActiveWorkoutUiAction.Finish -> finish()
            ActiveWorkoutUiAction.Discard -> discard()
        }
    }

    private fun moveExercise(exerciseId: Uuid, targetExerciseId: Uuid) {
        viewModelScope.launch {
            val updatedWorkout = reorderActiveWorkoutExercise(exerciseId, targetExerciseId)
            _uiState.update { it.copy(workout = updatedWorkout) }
        }
    }

    private fun removeExercise(exerciseId: Uuid) {
        viewModelScope.launch {
            val removedSetIds = _uiState.value.workout.exercises
                .firstOrNull { it.id == exerciseId }?.sets?.map { it.id }.orEmpty()
            val updatedWorkout = removeExerciseFromActiveWorkout(exerciseId)
            _uiState.update { state ->
                state.copy(
                    workout = updatedWorkout,
                    setInputs = state.setInputs - removedSetIds.toSet(),
                )
            }
        }
    }

    private fun removeSet(setId: Uuid) {
        viewModelScope.launch {
            val updatedWorkout = removeSetFromActiveWorkout(setId)
            _uiState.update { state ->
                state.copy(workout = updatedWorkout, setInputs = state.setInputs - setId)
            }
        }
    }

    private fun finish() {
        if (_uiState.value.isFinishing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isFinishing = true, finishError = null) }
            try {
                finishActiveWorkout(currentInstant())
                _events.send(ActiveWorkoutEvent.Finished)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isFinishing = false,
                        finishError = exception.message ?: "Unable to finish the workout.",
                    )
                }
            }
        }
    }

    private fun setCompleted(setId: Uuid, completed: Boolean) {
        viewModelScope.launch {
            val updatedWorkout = setActiveWorkoutSetCompletion(
                setId = setId,
                completed = completed,
                changedAt = currentInstant(),
            )
            _uiState.update { it.copy(workout = updatedWorkout) }
            if (completed) startRestTimer()
        }
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()
        val timerEndsAt = _uiState.value.workout.restTimerEndsAt ?: run {
            _uiState.update { it.copy(remainingRestSeconds = 0) }
            return
        }
        restTimerJob = viewModelScope.launch {
            while (true) {
                val remaining = (timerEndsAt - currentInstant()).inWholeSeconds
                    .coerceAtLeast(0)
                    .toInt()
                _uiState.update { it.copy(remainingRestSeconds = remaining) }
                if (remaining == 0) break
                delay(1_000)
            }
        }
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                val elapsed = (currentInstant() - _uiState.value.workout.startedAt)
                    .inWholeSeconds
                    .coerceAtLeast(0)
                    .coerceAtMost(Int.MAX_VALUE.toLong())
                    .toInt()
                _uiState.update { it.copy(elapsedWorkoutSeconds = elapsed) }
                delay(1_000)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun addSet(exerciseDraftId: Uuid) {
        viewModelScope.launch {
            val setId = Uuid.random()
            val updatedWorkout = addSetToActiveWorkout(
                exerciseDraftId = exerciseDraftId,
                setId = setId,
            )
            val newSet = updatedWorkout.exercises
                .flatMap { it.sets }
                .first { it.id == setId }
            _uiState.update {
                it.copy(
                    workout = updatedWorkout,
                    setInputs = it.setInputs + (
                        setId to SetInputUi(
                            weight = newSet.weight?.toInputString().orEmpty(),
                            reps = newSet.reps?.toString().orEmpty(),
                        )
                    ),
                )
            }
        }
    }

    private fun changeSetWeight(setId: Uuid, value: String) {
        if (!value.isValidDecimalInput()) return
        updateSetInput(setId) { it.copy(weight = value) }
        persistSet(setId)
    }

    private fun changeSetReps(setId: Uuid, value: String) {
        if (value.isNotEmpty() && (value.any { !it.isDigit() } || value.toIntOrNull() == null)) return
        updateSetInput(setId) { it.copy(reps = value) }
        persistSet(setId)
    }

    private fun updateSetInput(setId: Uuid, transform: (SetInputUi) -> SetInputUi) {
        _uiState.update { state ->
            val currentInput = state.setInputs[setId] ?: SetInputUi()
            state.copy(setInputs = state.setInputs + (setId to transform(currentInput)))
        }
    }

    private fun persistSet(setId: Uuid) {
        val input = _uiState.value.setInputs.getValue(setId)
        val weight = input.weight.toDoubleOrNull()
        if (input.weight.isNotEmpty() && weight == null) return

        viewModelScope.launch {
            val updatedWorkout = updateActiveWorkoutSet(
                setId = setId,
                reps = input.reps.toIntOrNull(),
                weight = weight,
            )
            _uiState.update { it.copy(workout = updatedWorkout) }
        }
    }

    private fun openExercisePicker() {
        _uiState.update { it.copy(isExercisePickerVisible = true) }
        if (_uiState.value.exercisePicker is ExercisePickerState.Idle) {
            loadExercises()
        }
    }

    private fun closeExercisePicker() {
        _uiState.update { it.copy(isExercisePickerVisible = false) }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(exercisePicker = ExercisePickerState.Loading) }
            try {
                val exercises = getExercises().sortedBy(Exercise::name)
                _uiState.update { it.copy(exercisePicker = ExercisePickerState.Data(exercises)) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        exercisePicker = ExercisePickerState.Error(
                            exception.message ?: "Unable to load exercises.",
                        ),
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            val updatedWorkout = addExerciseToActiveWorkout(
                draftExerciseId = Uuid.random(),
                exercise = exercise,
            )
            _uiState.update {
                it.copy(
                    workout = updatedWorkout,
                    isExercisePickerVisible = false,
                )
            }
            loadPreviousPerformances()
        }
    }

    private fun loadPreviousPerformances() {
        val exerciseIds = _uiState.value.workout.exercises.map { it.exerciseId }.toSet()
        if (exerciseIds.isEmpty()) return

        viewModelScope.launch {
            try {
                val performances = getPreviousExercisePerformances(exerciseIds)
                _uiState.update { it.copy(previousPerformances = performances) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Previous performance is supplementary and must not block the active workout.
            }
        }
    }

    private fun discard() {
        viewModelScope.launch {
            discardActiveWorkout()
            _events.send(ActiveWorkoutEvent.Closed)
        }
    }
}

private fun String.isValidDecimalInput(): Boolean {
    if (isEmpty()) return true
    if (count { it == '.' } > 1 || any { !it.isDigit() && it != '.' }) return false
    return this == "." || toDoubleOrNull() != null
}

private fun Double.toInputString(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()

sealed interface ActiveWorkoutEvent {
    data object Closed : ActiveWorkoutEvent
    data object Finished : ActiveWorkoutEvent
}
