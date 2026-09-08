package com.example.ogym_kmp.feature.workout.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ogym_kmp.feature.workout.presentation.home.component.QuickStartCard
import com.example.ogym_kmp.feature.workout.presentation.home.component.WorkoutHistoryCard
import com.example.ogym_kmp.feature.workout.presentation.home.component.WorkoutTemplateCard
import com.example.ogym_kmp.core.common.currentDateLabel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onWorkoutStarted: (com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.WorkoutStarted -> onWorkoutStarted(event.draft)
            }
        }
    }
    HomeContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateLabel = remember { currentDateLabel() }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = dateLabel.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Ready to train?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        item { QuickStartCard { onAction(HomeUiAction.StartEmptyWorkout) } }

        item {
            Section(title = "Workout templates") {
                when (val templates = state.templates) {
                    Loadable.Loading -> SectionLoading()
                    is Loadable.Error -> SectionError(templates.message) {
                        onAction(HomeUiAction.RetryTemplates)
                    }
                    is Loadable.Data -> {
                        if (templates.value.isEmpty()) {
                            EmptyMessage("No workout template yet.")
                        } else {
                            BoxWithConstraints {
                                val cardWidth = (maxWidth - 12.dp) / 2
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    items(templates.value, key = { it.id.toString() }) { template ->
                                        WorkoutTemplateCard(
                                            template = template,
                                            modifier = Modifier.width(cardWidth),
                                        ) {
                                            onAction(HomeUiAction.SelectTemplate(template.id))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Section(title = "Workout history") {
                when (val sessions = state.sessions) {
                    Loadable.Loading -> SectionLoading()
                    is Loadable.Error -> SectionError(sessions.message) {
                        onAction(HomeUiAction.RetrySessions)
                    }
                    is Loadable.Data -> {
                        if (sessions.value.isEmpty()) {
                            EmptyMessage("Your completed workouts will appear here.")
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                sessions.value.forEach { session ->
                                    WorkoutHistoryCard(session) {
                                        onAction(HomeUiAction.SelectSession(session.id))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
private fun SectionLoading() {
    Box(Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SectionError(message: String, onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EmptyMessage(message: String) {
    Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
}
