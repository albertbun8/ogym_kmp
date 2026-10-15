package com.example.ogym_kmp.feature.workout.presentation.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ogym_kmp.core.common.currentInstant
import com.example.ogym_kmp.core.common.model.WeightUnit
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutExerciseDraft
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutSetDraft
import com.example.ogym_kmp.feature.workout.domain.model.CreateTemplateExerciseInput
import com.example.ogym_kmp.feature.workout.domain.model.CreateTemplateSetInput
import com.example.ogym_kmp.feature.workout.domain.model.CreateWorkoutTemplateInput
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.CreateWorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.UpdateWorkoutTemplate
import com.example.ogym_kmp.feature.workout.presentation.active.ExercisePickerState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TemplateEditorUiState(
    val name: String = "New template",
    val exercises: List<ActiveWorkoutExerciseDraft> = emptyList(),
    val inputs: Map<Uuid, TemplateSetInputUi> = emptyMap(),
    val pickerVisible: Boolean = false,
    val picker: ExercisePickerState = ExercisePickerState.Idle,
    val saving: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val loadingTemplate: Boolean = false,
)

data class TemplateSetInputUi(val weight: String = "", val reps: String = "")

sealed interface TemplateEditorAction {
    data class ChangeName(val value: String) : TemplateEditorAction
    data object OpenPicker : TemplateEditorAction
    data object ClosePicker : TemplateEditorAction
    data class SelectExercise(val exercise: Exercise) : TemplateEditorAction
    data class AddSet(val exerciseId: Uuid) : TemplateEditorAction
    data class MoveExercise(val exerciseId: Uuid, val targetExerciseId: Uuid) : TemplateEditorAction
    data class RemoveExercise(val exerciseId: Uuid) : TemplateEditorAction
    data class RemoveSet(val setId: Uuid) : TemplateEditorAction
    data class ChangeWeight(val setId: Uuid, val value: String) : TemplateEditorAction
    data class ChangeReps(val setId: Uuid, val value: String) : TemplateEditorAction
    data object Save : TemplateEditorAction
    data object Cancel : TemplateEditorAction
}

class TemplateEditorViewModel(
    private val getExercises: GetExercises,
    private val createWorkoutTemplate: CreateWorkoutTemplate,
    private val updateWorkoutTemplate: UpdateWorkoutTemplate,
    private val template: WorkoutTemplate? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TemplateEditorUiState(
            name = template?.name ?: "New template",
            isEditing = template != null,
            loadingTemplate = template != null,
        ),
    )
    val uiState = _uiState.asStateFlow()
    private val _events = Channel<TemplateEditorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        if (template != null) loadTemplate(template)
    }

    private fun loadTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            try {
                val exercisesById = getExercises().associateBy(Exercise::id)
                _uiState.update { state ->
                    state.copy(
                        exercises = template.exercises.sortedBy { it.positionInTemplate }.map { exercise ->
                            ActiveWorkoutExerciseDraft(
                                id = exercise.id,
                                exerciseId = exercise.exerciseId,
                                exerciseName = exercisesById[exercise.exerciseId]?.name ?: "Unknown exercise",
                                positionInWorkout = exercise.positionInTemplate,
                                muscleGroups = exercisesById[exercise.exerciseId]?.muscleGroups?.toSet().orEmpty(),
                                sets = exercise.sets.sortedBy { it.positionInExercise }.map { set ->
                                    ActiveWorkoutSetDraft(
                                        id = set.id,
                                        positionInExercise = set.positionInExercise,
                                        reps = set.targetReps,
                                        weight = set.targetWeight,
                                        weightUnit = set.targetWeightUnit,
                                        completed = false,
                                    )
                                },
                            )
                        },
                        inputs = template.exercises.flatMap { it.sets }.associate { set ->
                            set.id to TemplateSetInputUi(
                                weight = set.targetWeight.toString(),
                                reps = set.targetReps.toString(),
                            )
                        },
                        loadingTemplate = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadingTemplate = false, error = e.message ?: "Unable to load template.")
                }
            }
        }
    }

    fun onAction(action: TemplateEditorAction) = when (action) {
        is TemplateEditorAction.ChangeName -> _uiState.update { it.copy(name = action.value) }
        TemplateEditorAction.OpenPicker -> openPicker()
        TemplateEditorAction.ClosePicker -> _uiState.update { it.copy(pickerVisible = false) }
        is TemplateEditorAction.SelectExercise -> addExercise(action.exercise)
        is TemplateEditorAction.AddSet -> addSet(action.exerciseId)
        is TemplateEditorAction.MoveExercise -> moveExercise(action.exerciseId, action.targetExerciseId)
        is TemplateEditorAction.RemoveExercise -> removeExercise(action.exerciseId)
        is TemplateEditorAction.RemoveSet -> removeSet(action.setId)
        is TemplateEditorAction.ChangeWeight -> changeInput(action.setId, weight = action.value)
        is TemplateEditorAction.ChangeReps -> changeInput(action.setId, reps = action.value)
        TemplateEditorAction.Save -> save()
        TemplateEditorAction.Cancel -> viewModelScope.launch { _events.send(TemplateEditorEvent.Closed) }
    }

    private fun openPicker() {
        _uiState.update { it.copy(pickerVisible = true, picker = ExercisePickerState.Loading) }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(picker = ExercisePickerState.Data(getExercises())) }
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _uiState.update { it.copy(picker = ExercisePickerState.Error(e.message ?: "Unable to load exercises.")) } }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun addExercise(exercise: Exercise) {
        _uiState.update { state ->
            state.copy(
                pickerVisible = false,
                exercises = state.exercises + ActiveWorkoutExerciseDraft(
                    Uuid.random(), exercise.id, exercise.name, state.exercises.size,
                    exercise.muscleGroups.toSet(), emptyList(),
                ),
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun addSet(exerciseId: Uuid) {
        val setId = Uuid.random()
        _uiState.update { state ->
            val exercise = state.exercises.first { it.id == exerciseId }
            val previousInput = exercise.sets.lastOrNull()?.let { state.inputs[it.id] }
            val newInput = previousInput ?: TemplateSetInputUi(weight = "0", reps = "0")
            state.copy(
                exercises = state.exercises.map { currentExercise ->
                    if (currentExercise.id == exerciseId) currentExercise.copy(
                        sets = currentExercise.sets + ActiveWorkoutSetDraft(
                            setId,
                            currentExercise.sets.size,
                            newInput.reps.toIntOrNull() ?: 0,
                            newInput.weight.toDoubleOrNull() ?: 0.0,
                            currentExercise.sets.lastOrNull()?.weightUnit ?: WeightUnit.LB,
                            false,
                        ),
                    ) else currentExercise
                },
                inputs = state.inputs + (setId to newInput),
            )
        }
    }

    private fun moveExercise(exerciseId: Uuid, targetExerciseId: Uuid) {
        if (exerciseId == targetExerciseId) return
        _uiState.update { state ->
            val fromIndex = state.exercises.indexOfFirst { it.id == exerciseId }
            val toIndex = state.exercises.indexOfFirst { it.id == targetExerciseId }
            if (fromIndex == -1 || toIndex == -1) return@update state
            val reordered = state.exercises.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }.mapIndexed { index, exercise -> exercise.copy(positionInWorkout = index) }
            state.copy(exercises = reordered)
        }
    }

    private fun removeExercise(exerciseId: Uuid) {
        _uiState.update { state ->
            val removedSetIds = state.exercises.firstOrNull { it.id == exerciseId }
                ?.sets?.map { it.id }?.toSet().orEmpty()
            state.copy(
                exercises = state.exercises
                    .filterNot { it.id == exerciseId }
                    .mapIndexed { index, exercise -> exercise.copy(positionInWorkout = index) },
                inputs = state.inputs - removedSetIds,
            )
        }
    }

    private fun removeSet(setId: Uuid) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map { exercise ->
                    exercise.copy(
                        sets = exercise.sets
                            .filterNot { it.id == setId }
                            .mapIndexed { index, set -> set.copy(positionInExercise = index) },
                    )
                },
                inputs = state.inputs - setId,
            )
        }
    }

    private fun changeInput(setId: Uuid, weight: String? = null, reps: String? = null) {
        if (weight != null && !weight.matches(Regex("\\d*(\\.\\d*)?"))) return
        if (reps != null && reps.any { !it.isDigit() }) return
        _uiState.update { state ->
            val old = state.inputs[setId] ?: TemplateSetInputUi()
            state.copy(inputs = state.inputs + (setId to old.copy(weight = weight ?: old.weight, reps = reps ?: old.reps)))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, error = null) }
            try {
                val input = CreateWorkoutTemplateInput(
                    template?.id ?: Uuid.random(), state.name,
                    state.exercises.map { exercise ->
                        CreateTemplateExerciseInput(
                            exercise.id, exercise.exerciseId, exercise.positionInWorkout,
                            exercise.sets.map { set ->
                                val values = state.inputs.getValue(set.id)
                                CreateTemplateSetInput(
                                    set.id, set.positionInExercise,
                                    checkNotNull(values.reps.toIntOrNull()) { "Every set requires repetitions." },
                                    checkNotNull(values.weight.toDoubleOrNull()) { "Every set requires weight." },
                                    set.weightUnit,
                                )
                            },
                        )
                    },
                )
                if (template == null) {
                    createWorkoutTemplate(input, currentInstant())
                } else {
                    updateWorkoutTemplate(input, currentInstant())
                }
                _events.send(TemplateEditorEvent.Saved)
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        saving = false,
                        error = e.message ?: if (template == null) {
                            "Unable to create template."
                        } else {
                            "Unable to update template."
                        },
                    )
                }
            }
        }
    }
}

sealed interface TemplateEditorEvent { data object Closed : TemplateEditorEvent; data object Saved : TemplateEditorEvent }
