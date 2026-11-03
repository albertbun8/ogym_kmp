package com.example.ogym_kmp.feature.workout.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ogym_kmp.core.common.currentDateIso
import com.example.ogym_kmp.core.common.currentInstant
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutSession
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutSessions
import com.example.ogym_kmp.feature.workout.domain.usecase.GetWorkoutTemplates
import com.example.ogym_kmp.feature.workout.domain.usecase.StartEmptyWorkout
import com.example.ogym_kmp.feature.workout.domain.usecase.StartWorkoutFromTemplate
import com.example.ogym_kmp.feature.workout.domain.usecase.DeleteWorkoutTemplate
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class HomeViewModel(
    private val getWorkoutTemplates: GetWorkoutTemplates,
    private val getWorkoutSessions: GetWorkoutSessions,
    private val startEmptyWorkout: StartEmptyWorkout,
    private val startWorkoutFromTemplate: StartWorkoutFromTemplate,
    private val getExercises: GetExercises,
    private val deleteWorkoutTemplate: DeleteWorkoutTemplate,
) : ViewModel() {
    private var loadedTemplates: Map<Uuid, WorkoutTemplate> = emptyMap()
    private var startWorkoutJob: Job? = null
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        preloadExercises()
        loadTemplates()
        loadSessions()
    }

    private fun preloadExercises() {
        viewModelScope.launch {
            try {
                getExercises()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Exercise preloading is best-effort and must not block Home.
            }
        }
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.RetryTemplates -> loadTemplates()
            HomeUiAction.RetrySessions -> loadSessions()
            HomeUiAction.StartEmptyWorkout -> startEmptyWorkout()
            HomeUiAction.CreateTemplate -> viewModelScope.launch { _events.send(HomeEvent.CreateTemplate) }
            is HomeUiAction.EditTemplate -> loadedTemplates[action.id]?.let { template ->
                viewModelScope.launch { _events.send(HomeEvent.EditTemplate(template)) }
            }

            is HomeUiAction.DeleteTemplate -> deleteTemplate(action.id)
            is HomeUiAction.SelectTemplate -> startWorkoutFromTemplate(action.id)
            is HomeUiAction.SelectSession -> toggleSession(action.id)
        }
    }

    private fun deleteTemplate(templateId: Uuid) {
        viewModelScope.launch {
            _uiState.update { it.copy(templateActionError = null) }
            try {
                deleteWorkoutTemplate(templateId)
                loadedTemplates = loadedTemplates - templateId
                loadTemplates()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(templateActionError = exception.message ?: "Unable to delete template.")
                }
            }
        }
    }

    private fun toggleSession(sessionId: Uuid) {
        _uiState.update {
            it.copy(expandedSessionId = sessionId.takeUnless { id -> id == it.expandedSessionId })
        }
    }

    private fun startEmptyWorkout() {
        if (startWorkoutJob?.isActive == true) return
        startWorkoutJob = viewModelScope.launch {
            _uiState.update { it.copy(workoutStartError = null) }
            try {
                val draft = startEmptyWorkout(
                    id = Uuid.random(),
                    startedAt = currentInstant(),
                    localDate = currentDateIso(),
                )
                _events.send(HomeEvent.WorkoutStarted(draft))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        workoutStartError = exception.message
                            ?: "Unable to start the empty workout.",
                    )
                }
            }
        }
    }

    private fun startWorkoutFromTemplate(templateId: Uuid) {
        if (startWorkoutJob?.isActive == true) return
        startWorkoutJob = viewModelScope.launch {
            _uiState.update { it.copy(workoutStartError = null) }
            try {
                val draft = startWorkoutFromTemplate(
                    templateId = templateId,
                    startedAt = currentInstant(),
                )
                _events.send(HomeEvent.WorkoutStarted(draft))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        workoutStartError = exception.message ?: "Unable to start the workout.",
                    )
                }
            }
        }
    }

    fun refreshSessions() {
        loadSessions()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            _uiState.update { it.copy(templates = Loadable.Loading) }
            try {
                val templates = getWorkoutTemplates()
                loadedTemplates = templates.associateBy(WorkoutTemplate::id)
                _uiState.update { it.copy(templates = Loadable.Data(templates.map(WorkoutTemplate::toUi))) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        templates = Loadable.Error(
                            exception.message ?: "Unable to load templates."
                        )
                    )
                }
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(sessions = Loadable.Loading) }
            try {
                val sessions = getWorkoutSessions().sortedByDescending { it.startedAt }
                    .map(WorkoutSession::toUi)
                _uiState.update { it.copy(sessions = Loadable.Data(sessions)) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        sessions = Loadable.Error(
                            exception.message ?: "Unable to load history."
                        )
                    )
                }
            }
        }
    }
}

sealed interface HomeEvent {
    data class WorkoutStarted(val draft: ActiveWorkoutDraft) : HomeEvent
    data object CreateTemplate : HomeEvent
    data class EditTemplate(val template: WorkoutTemplate) : HomeEvent
}

private fun WorkoutTemplate.toUi() = WorkoutTemplateItemUi(
    id = id,
    name = name,
    exerciseCount = exercises.size,
    setCount = exercises.sumOf { it.sets.size },
    isSystemTemplate = ownerId == null,
)

private fun WorkoutSession.toUi(): WorkoutSessionItemUi {
    val durationMinutes = completedAt?.let { (it - startedAt).inWholeMinutes }
    val isoDate = startedAt.toString()
    return WorkoutSessionItemUi(
        id = id,
        isoDate = isoDate.substring(0, 10),
        name = name,
        dayLabel = isoDate.substring(8, 10),
        monthLabel = isoDate.substring(5, 7).toMonthLabel(),
        durationLabel = durationMinutes?.let { "$it min" },
        exerciseCount = exercises.size,
        completedSetCount = exercises.sumOf { exercise -> exercise.sets.count { it.completed } },
        dateLabel = "${isoDate.substring(5, 7).toMonthName()} ${
            isoDate.substring(8, 10).trimStart('0')
        }, ${isoDate.substring(0, 4)}",
        exercises = exercises.sortedBy { it.positionInWorkout }.map { exercise ->
            val completedSets =
                exercise.sets.filter { it.completed }.sortedBy { it.positionInExercise }
            val repetitions = completedSets.map { it.reps }
            WorkoutHistoryExerciseUi(
                name = exercise.exerciseName,
                setCount = completedSets.size,
                repsLabel = if (repetitions.distinct().size <= 1) {
                    repetitions.firstOrNull()?.toString().orEmpty()
                } else {
                    repetitions.joinToString("/")
                },
            )
        },
    )
}

private fun String.toMonthName(): String = when (this) {
    "01" -> "Jan"
    "02" -> "Feb"
    "03" -> "Mar"
    "04" -> "Apr"
    "05" -> "May"
    "06" -> "Jun"
    "07" -> "Jul"
    "08" -> "Aug"
    "09" -> "Sep"
    "10" -> "Oct"
    "11" -> "Nov"
    "12" -> "Dec"
    else -> ""
}

private fun String.toMonthLabel(): String = when (this) {
    "01" -> "JAN"
    "02" -> "FEB"
    "03" -> "MAR"
    "04" -> "APR"
    "05" -> "MAY"
    "06" -> "JUN"
    "07" -> "JUL"
    "08" -> "AUG"
    "09" -> "SEP"
    "10" -> "OCT"
    "11" -> "NOV"
    "12" -> "DEC"
    else -> ""
}
