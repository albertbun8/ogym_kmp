package com.example.ogym_kmp.feature.exercise.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ogym_kmp.feature.exercise.domain.usecase.GetExercises
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val getExercises: GetExercises
) : ViewModel() {
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    init{
        loadExercises()
    }

    fun loadExercises(){
        viewModelScope.launch{
            _uiState.value = ExerciseUiState.Loading
            try{
                val exercises = getExercises()
                _uiState.value = ExerciseUiState.Success(exercises)
            } catch(exception: CancellationException){
                throw exception
            } catch(exception: Exception){
                _uiState.value = ExerciseUiState.Error(
                    message = exception.message?: "Unable to load exercises."
                )
            }
        }
    }
}