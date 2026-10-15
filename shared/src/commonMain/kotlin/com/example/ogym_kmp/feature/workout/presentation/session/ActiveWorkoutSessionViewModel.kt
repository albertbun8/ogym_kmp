package com.example.ogym_kmp.feature.workout.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.usecase.GetActiveWorkout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActiveWorkoutSessionViewModel(
    private val getActiveWorkout: GetActiveWorkout
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<ActiveWorkoutSessionUiState>(ActiveWorkoutSessionUiState.Loading)
    val uiState: StateFlow<ActiveWorkoutSessionUiState> = _uiState.asStateFlow()

    init {
        preloadActiveWorkout()
    }

    private fun preloadActiveWorkout() {
        viewModelScope.launch {
            try {
                val result = getActiveWorkout()
                if (result != null) {
                    _uiState.value = ActiveWorkoutSessionUiState.Active(result)
                } else {
                    _uiState.value = ActiveWorkoutSessionUiState.NoActiveWorkout
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = ActiveWorkoutSessionUiState.NoActiveWorkout
                println(exception)
            }
        }
    }

    fun onWorkoutStarted(workout: ActiveWorkoutDraft) {
        _uiState.value = ActiveWorkoutSessionUiState.Active(workout)
    }

    fun onWorkoutEnded() {
        _uiState.value = ActiveWorkoutSessionUiState.NoActiveWorkout
    }
}