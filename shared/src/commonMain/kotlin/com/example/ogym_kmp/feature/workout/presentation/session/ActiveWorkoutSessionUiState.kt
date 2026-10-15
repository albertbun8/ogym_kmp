package com.example.ogym_kmp.feature.workout.presentation.session

import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft

sealed interface ActiveWorkoutSessionUiState {
    data object Loading : ActiveWorkoutSessionUiState
    data object NoActiveWorkout : ActiveWorkoutSessionUiState
    data class Active(val workout: ActiveWorkoutDraft) : ActiveWorkoutSessionUiState
}
