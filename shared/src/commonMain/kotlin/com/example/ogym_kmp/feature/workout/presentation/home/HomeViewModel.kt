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
import kotlinx.coroutines.CancellationException
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadTemplates()
        loadSessions()
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.RetryTemplates -> loadTemplates()
            HomeUiAction.RetrySessions -> loadSessions()
            HomeUiAction.StartEmptyWorkout -> startEmptyWorkout()
            is HomeUiAction.SelectTemplate,
            is HomeUiAction.SelectSession -> Unit
        }
    }

    fun refreshSessions() {
        loadSessions()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun startEmptyWorkout() {
        viewModelScope.launch {
            val draft = startEmptyWorkout(
                id = Uuid.random(),
                startedAt = currentInstant(),
                localDate = currentDateIso(),
            )
            _events.send(HomeEvent.WorkoutStarted(draft))
        }
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            _uiState.update { it.copy(templates = Loadable.Loading) }
            try {
                _uiState.update {
                    it.copy(templates = Loadable.Data(getWorkoutTemplates().map(WorkoutTemplate::toUi)))
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(templates = Loadable.Error(exception.message ?: "Unable to load templates."))
                }
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(sessions = Loadable.Loading) }
            try {
                val sessions = getWorkoutSessions().sortedByDescending { it.startedAt }.map(WorkoutSession::toUi)
                _uiState.update { it.copy(sessions = Loadable.Data(sessions)) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(sessions = Loadable.Error(exception.message ?: "Unable to load history."))
                }
            }
        }
    }
}

sealed interface HomeEvent {
    data class WorkoutStarted(val draft: ActiveWorkoutDraft) : HomeEvent
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
        name = name,
        dayLabel = isoDate.substring(8, 10),
        monthLabel = isoDate.substring(5, 7).toMonthLabel(),
        durationLabel = durationMinutes?.let { "$it min" },
        exerciseCount = exercises.size,
        completedSetCount = exercises.sumOf { exercise -> exercise.sets.count { it.completed } },
    )
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
