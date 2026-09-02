package com.example.ogym_kmp.feature.exercise.presentation

import com.example.ogym_kmp.feature.exercise.domain.model.Exercise

sealed interface ExerciseUiState {
    data object Loading : ExerciseUiState
    data class Success(val exercises: List<Exercise>) : ExerciseUiState
    data class Error(val message: String) : ExerciseUiState
}