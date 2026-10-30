package com.example.ogym_kmp.feature.workout.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ogym_kmp.feature.workout.presentation.home.component.QuickStartCard
import com.example.ogym_kmp.feature.workout.presentation.home.component.WeeklyActivityCalendar
import com.example.ogym_kmp.feature.workout.presentation.home.component.WorkoutHistoryCard
import com.example.ogym_kmp.feature.workout.presentation.home.component.WorkoutTemplateCard
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.domain.model.WorkoutTemplate
import com.example.ogym_kmp.core.common.currentWeekDates
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    activeWorkout: ActiveWorkoutDraft?,
    onWorkoutStarted: (ActiveWorkoutDraft) -> Unit,
    onCreateTemplate: () -> Unit,
    onEditTemplate: (WorkoutTemplate) -> Unit,
    canStartWorkout: Boolean,
    onStartBlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnWorkoutStarted by rememberUpdatedState(onWorkoutStarted)
    val currentOnCreateTemplate by rememberUpdatedState(onCreateTemplate)
    val currentOnEditTemplate by rememberUpdatedState(onEditTemplate)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.WorkoutStarted -> currentOnWorkoutStarted(event.draft)
                HomeEvent.CreateTemplate -> currentOnCreateTemplate()
                is HomeEvent.EditTemplate -> currentOnEditTemplate(event.template)
            }
        }
    }
    HomeContent(
        state = state,
        activeWorkout = activeWorkout,
        onAction = { action ->
            val startsWorkout = action is HomeUiAction.StartEmptyWorkout ||
                    action is HomeUiAction.SelectTemplate
            if (startsWorkout && !canStartWorkout) {
                onStartBlocked()
            } else {
                viewModel.onAction(action)
            }
        },
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    activeWorkout: ActiveWorkoutDraft? = null,
    onAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var templatePendingDeletion by remember { mutableStateOf<WorkoutTemplateItemUi?>(null) }
    templatePendingDeletion?.let { template ->
        AlertDialog(
            onDismissRequest = { templatePendingDeletion = null },
            title = { Text("Delete template?") },
            text = { Text("${template.name} will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        templatePendingDeletion = null
                        onAction(HomeUiAction.DeleteTemplate(template.id))
                    },
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { templatePendingDeletion = null }) { Text("Cancel") }
            },
        )
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val templates = (state.templates as? Loadable.Data)?.value.orEmpty()
    val sessions = (state.sessions as? Loadable.Data)?.value.orEmpty()
    val weekDateIds = remember { currentWeekDates().mapTo(mutableSetOf()) { it.isoDate } }
    val weeklySessions = sessions.filter { it.isoDate in weekDateIds }
    val weeklySetCount = weeklySessions.sumOf { it.completedSetCount }
    val proposedTemplate = templates.firstOrNull()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "YOUR TRAINING",
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Consistency builds a stronger you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            val completedDates = when (val sessions = state.sessions) {
                is Loadable.Data -> sessions.value.mapTo(mutableSetOf()) { it.isoDate }
                else -> emptySet()
            }
            ThisWeekCard(
                completedDates = completedDates,
                workoutCount = weeklySessions.size,
                setCount = weeklySetCount,
                activeDays = weeklySessions.map { it.isoDate }.distinct().size,
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickStartCard(
                    activeWorkout = activeWorkout,
                    proposedTemplate = proposedTemplate,
                    onStartClick = {
                        if (proposedTemplate == null) {
                            onAction(HomeUiAction.StartEmptyWorkout)
                        } else {
                            onAction(HomeUiAction.SelectTemplate(proposedTemplate.id))
                        }
                    },
                )
                HomeQuickActions(
                    onEmptyWorkout = { onAction(HomeUiAction.StartEmptyWorkout) },
                    onTemplates = { coroutineScope.launch { listState.animateScrollToItem(3) } },
                    onHistory = { coroutineScope.launch { listState.animateScrollToItem(5) } },
                )
                state.workoutStartError?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        item {
            Section(
                title = "My templates",
                action = {
                    FilledIconButton(
                        onClick = { onAction(HomeUiAction.CreateTemplate) },
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Create template")
                    }
                },
            ) {
                state.templateActionError?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error)
                }
                when (val templates = state.templates) {
                    Loadable.Loading -> SectionLoading()
                    is Loadable.Error -> SectionError(templates.message) {
                        onAction(HomeUiAction.RetryTemplates)
                    }
                    is Loadable.Data -> {
                        if (templates.value.isEmpty()) {
                            EmptyMessage("No workout template yet.")
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                templates.value.forEach { template ->
                                    WorkoutTemplateCard(
                                        template = template,
                                        onClick = { onAction(HomeUiAction.SelectTemplate(template.id)) },
                                        onEdit = { onAction(HomeUiAction.EditTemplate(template.id)) },
                                        onDelete = { templatePendingDeletion = template },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            LatestAchievementSection(sessions.firstOrNull())
        }

        item {
            Section(title = "Recent activity") {
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
                                    WorkoutHistoryCard(
                                        session = session,
                                        expanded = state.expandedSessionId == session.id,
                                    ) {
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
private fun ThisWeekCard(
    completedDates: Set<String>,
    workoutCount: Int,
    setCount: Int,
    activeDays: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "THIS WEEK", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "$workoutCount of $WEEKLY_WORKOUT_GOAL workouts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            WeeklyActivityCalendar(completedDates = completedDates)
            LinearProgressIndicator(
                progress = { (workoutCount / WEEKLY_WORKOUT_GOAL.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                WeekMetric(setCount.toString(), "Sets", Modifier.weight(1f))
                WeekMetric(activeDays.toString(), "Active days", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WeekMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = OGymTheme.colors.accent,
        )
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 3.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HomeQuickActions(
    onEmptyWorkout: () -> Unit,
    onTemplates: () -> Unit,
    onHistory: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickAction(Icons.Outlined.AddBox, "Empty workout", onEmptyWorkout, Modifier.weight(1f))
        QuickAction(Icons.Outlined.ContentCopy, "Templates", onTemplates, Modifier.weight(1f))
        QuickAction(Icons.Outlined.History, "History", onHistory, Modifier.weight(1f))
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = OGymTheme.colors.accent,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LatestAchievementSection(session: WorkoutSessionItemUi?) {
    Section(title = "Latest achievement") {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = OGymTheme.colors.achievement,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = session?.let { "Workout completed · ${it.name}" }
                            ?: "Complete a workout to earn your first achievement",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    session?.let {
                        Text(
                            text = "${it.completedSetCount} sets · ${it.dateLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private const val WEEKLY_WORKOUT_GOAL = 4

@Composable
private fun Section(
    title: String,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title.uppercase(), style = MaterialTheme.typography.titleLarge)
            action?.invoke()
        }
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
