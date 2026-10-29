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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocalFireDepartment
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    activeWorkout: ActiveWorkoutDraft?,
    onWorkoutStarted: (ActiveWorkoutDraft) -> Unit,
    onCreateTemplate: () -> Unit,
    onEditTemplate: (WorkoutTemplate) -> Unit,
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
        onAction = viewModel::onAction,
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
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            val completedDates = when (val sessions = state.sessions) {
                is Loadable.Data -> sessions.value.mapTo(mutableSetOf()) { it.isoDate }
                else -> emptySet()
            }
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                WeeklyActivityCalendar(completedDates = completedDates)
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "Your training",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "Consistency builds a stronger you.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
            ThisWeekSection(
                workoutCount = weeklySessions.size,
                setCount = weeklySetCount,
                activeDays = weeklySessions.map { it.isoDate }.distinct().size,
            )
        }

        item {
            Section(
                title = "My Templates",
                action = {
                    FilledIconButton(
                        onClick = { onAction(HomeUiAction.CreateTemplate) },
                        modifier = Modifier
                            .size(40.dp)
                            .semantics { contentDescription = "Create template" },
                    ) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
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
                                templates.value.chunked(2).forEach { rowTemplates ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        rowTemplates.forEach { template ->
                                            WorkoutTemplateCard(
                                                template = template,
                                                modifier = Modifier.weight(1f),
                                                onClick = { onAction(HomeUiAction.SelectTemplate(template.id)) },
                                                onEdit = { onAction(HomeUiAction.EditTemplate(template.id)) },
                                                onDelete = { templatePendingDeletion = template },
                                            )
                                        }
                                        if (rowTemplates.size == 1) Spacer(Modifier.weight(1f))
                                    }
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
private fun HomeQuickActions(
    onEmptyWorkout: () -> Unit,
    onTemplates: () -> Unit,
    onHistory: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuickAction(Icons.Outlined.AddBox, "Empty workout", onEmptyWorkout, Modifier.weight(1f))
        QuickActionDivider()
        QuickAction(Icons.Outlined.ContentCopy, "Templates", onTemplates, Modifier.weight(1f))
        QuickActionDivider()
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
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun QuickActionDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 46.dp)
            .background(HomeTrackColor),
    )
}

@Composable
private fun ThisWeekSection(
    workoutCount: Int,
    setCount: Int,
    activeDays: Int,
) {
    Section(
        title = "This week",
        action = {
            Text(
                text = "See more  ›",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeekMetric(Icons.Outlined.FitnessCenter, workoutCount.toString(), "Workouts", Modifier.weight(1f))
            MetricDivider()
            WeekMetric(Icons.Outlined.Layers, setCount.toString(), "Sets", Modifier.weight(1f))
            MetricDivider()
            WeekMetric(Icons.Outlined.LocalFireDepartment, activeDays.toString(), "Active days", Modifier.weight(1f))
        }
        LinearProgressIndicator(
            progress = { (workoutCount / 4f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(5.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = HomeTrackColor,
        )
        Text(
            text = "$workoutCount of 4 workouts",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun WeekMetric(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.size(7.dp))
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MetricDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 40.dp)
            .background(HomeTrackColor),
    )
}

@Composable
private fun LatestAchievementSection(session: WorkoutSessionItemUi?) {
    Section(title = "Latest achievement") {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, HomeTrackColor),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("★", color = MaterialTheme.colorScheme.primary)
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = session?.let { "Workout completed  ·  ${it.name}" }
                            ?: "Complete a workout to earn your first achievement",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    session?.let {
                        Text(
                            text = "${it.completedSetCount} sets  ·  ${it.dateLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private val HomeTrackColor = Color(0xFFE5E7EB)

@Composable
private fun Section(
    title: String,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
