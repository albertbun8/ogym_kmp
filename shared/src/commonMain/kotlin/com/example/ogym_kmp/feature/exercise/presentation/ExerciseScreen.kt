package com.example.ogym_kmp.feature.exercise.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        ExerciseUiState.Loading -> {
            Text("Loading exercises...")
        }

        is ExerciseUiState.Success -> {
            LazyColumn {
                items(state.exercises) { exercise ->
                    Text(exercise.name)
                }
            }
        }

        is ExerciseUiState.Error -> {
            Text(state.message)
        }
    }
}