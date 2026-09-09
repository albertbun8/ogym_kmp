package com.example.ogym_kmp.feature.workout.presentation.home

import kotlin.uuid.Uuid

sealed interface Loadable<out T> {
    data object Loading : Loadable<Nothing>
    data class Data<T>(val value: T) : Loadable<T>
    data class Error(val message: String) : Loadable<Nothing>
}

data class WorkoutTemplateItemUi(
    val id: Uuid,
    val name: String,
    val exerciseCount: Int,
    val setCount: Int,
    val isSystemTemplate: Boolean,
)

data class WorkoutSessionItemUi(
    val id: Uuid,
    val name: String,
    val dayLabel: String,
    val monthLabel: String,
    val durationLabel: String?,
    val exerciseCount: Int,
    val completedSetCount: Int,
    val dateLabel: String,
    val exercises: List<WorkoutHistoryExerciseUi>,
)

data class WorkoutHistoryExerciseUi(
    val name: String,
    val setCount: Int,
    val repsLabel: String,
)

data class HomeUiState(
    val templates: Loadable<List<WorkoutTemplateItemUi>> = Loadable.Loading,
    val sessions: Loadable<List<WorkoutSessionItemUi>> = Loadable.Loading,
    val workoutStartError: String? = null,
    val expandedSessionId: Uuid? = null,
)

sealed interface HomeUiAction {
    data object RetryTemplates : HomeUiAction
    data object RetrySessions : HomeUiAction
    data object StartEmptyWorkout : HomeUiAction
    data object CreateTemplate : HomeUiAction
    data class SelectTemplate(val id: Uuid) : HomeUiAction
    data class SelectSession(val id: Uuid) : HomeUiAction
}
