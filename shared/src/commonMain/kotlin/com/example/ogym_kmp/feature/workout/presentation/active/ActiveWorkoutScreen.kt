package com.example.ogym_kmp.feature.workout.presentation.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.workout.presentation.component.ExercisePickerDialog
import com.example.ogym_kmp.feature.workout.presentation.component.SwipeToDelete

@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
    onClosed: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ActiveWorkoutEvent.Closed -> onClosed()
                ActiveWorkoutEvent.Finished -> onFinished()
            }
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var draggedExerciseId by remember { mutableStateOf<kotlin.uuid.Uuid?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    val reorderThreshold = with(LocalDensity.current) { 72.dp.toPx() }
    val defaultViewConfiguration = LocalViewConfiguration.current
    val completedSetCount = state.workout.exercises.sumOf { exercise ->
        exercise.sets.count { it.completed }
    }
    val totalSetCount = state.workout.exercises.sumOf { it.sets.size }
    val workoutProgress = if (totalSetCount == 0) 0f else completedSetCount.toFloat() / totalSetCount
    val reorderViewConfiguration = remember(defaultViewConfiguration) {
        object : ViewConfiguration by defaultViewConfiguration {
            override val longPressTimeoutMillis: Long = 1_000L
        }
    }

    if (state.isExercisePickerVisible) {
        ExercisePickerDialog(
            state = state.exercisePicker,
            onExerciseSelected = { viewModel.onAction(ActiveWorkoutUiAction.SelectExercise(it)) },
            onRetry = { viewModel.onAction(ActiveWorkoutUiAction.RetryExercises) },
            onDismiss = { viewModel.onAction(ActiveWorkoutUiAction.CloseExercisePicker) },
        )
    }

    CompositionLocalProvider(LocalViewConfiguration provides reorderViewConfiguration) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = state.workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "● ${state.elapsedWorkoutSeconds.toDurationLabel()}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "Rest ${state.remainingRestSeconds.toTimerLabel()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Button(
                onClick = { viewModel.onAction(ActiveWorkoutUiAction.Finish) },
                enabled = !state.isFinishing,
            ) {
                Text(if (state.isFinishing) "Finishing…" else "Finish")
            }
        }

        state.finishError?.let { message ->
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 18.dp),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "${state.workout.exercises.size} exercises  ·  $completedSetCount / $totalSetCount sets",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        LinearProgressIndicator(
            progress = { workoutProgress },
            modifier = Modifier.fillMaxWidth().height(5.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
        )

        if (state.workout.exercises.isEmpty()) {
            Text(
                text = "This workout is empty. Add your first exercise to begin tracking sets.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            state.workout.exercises.forEach { exercise ->
                val dragging = draggedExerciseId == exercise.id
                var menuExpanded by remember(exercise.id) { mutableStateOf(false) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (dragging) 1f else 0f)
                        .graphicsLayer {
                            translationY = if (dragging) dragOffset else 0f
                            scaleX = if (dragging) 1.02f else 1f
                            scaleY = if (dragging) 1.02f else 1f
                        },
                    shape = MaterialTheme.shapes.large,
                    color = if (dragging) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface,
                    contentColor = if (dragging) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface,
                    border = if (dragging) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    tonalElevation = 0.dp,
                    shadowElevation = if (dragging) 8.dp else 0.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(exercise.id, state.workout.exercises) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggedExerciseId = exercise.id
                                            dragOffset = 0f
                                        },
                                        onDragEnd = {
                                            draggedExerciseId = null
                                            dragOffset = 0f
                                        },
                                        onDragCancel = {
                                            draggedExerciseId = null
                                            dragOffset = 0f
                                        },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            dragOffset += amount.y
                                            val exercises = state.workout.exercises
                                            val currentIndex = exercises.indexOfFirst { it.id == exercise.id }
                                            val targetIndex = when {
                                                dragOffset > reorderThreshold -> currentIndex + 1
                                                dragOffset < -reorderThreshold -> currentIndex - 1
                                                else -> -1
                                            }
                                            if (currentIndex >= 0 && targetIndex in exercises.indices) {
                                                viewModel.onAction(
                                                    ActiveWorkoutUiAction.MoveExercise(
                                                        exercise.id,
                                                        exercises[targetIndex].id,
                                                    ),
                                                )
                                                dragOffset = 0f
                                            }
                                        },
                                    )
                                }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(exercise.exerciseName, fontWeight = FontWeight.SemiBold)
                            if (dragging) {
                                Text(
                                    text = "Dragging",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                androidx.compose.foundation.layout.Box {
                                    IconButton(onClick = { menuExpanded = true }) { Text("⋮") }
                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false },
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Remove exercise") },
                                            onClick = {
                                                menuExpanded = false
                                                viewModel.onAction(
                                                    ActiveWorkoutUiAction.RemoveExercise(exercise.id),
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = exercise.muscleGroups.joinToString { it.displayName() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (exercise.sets.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Set", style = MaterialTheme.typography.labelSmall)
                                Text("Previous", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    state.workout.preferredWeightUnit.name.lowercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                                Text("Reps", style = MaterialTheme.typography.labelSmall)
                                Text("Done", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        exercise.sets.forEach { set ->
                            val input = state.setInputs[set.id] ?: SetInputUi(
                                weight = set.weight?.toEditableString().orEmpty(),
                                reps = set.reps?.toString().orEmpty(),
                            )
                            val canComplete = input.weight.toDoubleOrNull() != null &&
                                input.reps.toIntOrNull() != null
                            val previous = state.previousPerformances[exercise.exerciseId]
                                ?.firstOrNull { it.positionInExercise == set.positionInExercise }
                            SwipeToDelete(
                                itemKey = set.id,
                                onDelete = {
                                    viewModel.onAction(ActiveWorkoutUiAction.RemoveSet(set.id))
                                },
                            ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = (set.positionInExercise + 1).toString(),
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = previous?.let {
                                        "${it.weight.toEditableString()} ${it.weightUnit.name.lowercase()} × ${it.reps}"
                                    } ?: "—",
                                    modifier = Modifier.width(74.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                OutlinedTextField(
                                    value = input.weight,
                                    onValueChange = {
                                        viewModel.onAction(ActiveWorkoutUiAction.ChangeSetWeight(set.id, it))
                                    },
                                    modifier = Modifier.width(58.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    enabled = !set.completed,
                                    colors = workoutFieldColors(),
                                )
                                OutlinedTextField(
                                    value = input.reps,
                                    onValueChange = {
                                        viewModel.onAction(ActiveWorkoutUiAction.ChangeSetReps(set.id, it))
                                    },
                                    modifier = Modifier.width(58.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    enabled = !set.completed,
                                    colors = workoutFieldColors(),
                                )
                                Checkbox(
                                    checked = set.completed,
                                    onCheckedChange = {
                                        viewModel.onAction(
                                            ActiveWorkoutUiAction.SetSetCompleted(set.id, it),
                                        )
                                    },
                                    enabled = set.completed || canComplete,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.secondary,
                                        checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                                    ),
                                )
                            }
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.onAction(ActiveWorkoutUiAction.AddSet(exercise.id)) },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            border = null,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Text("Add set")
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Button(
                onClick = { viewModel.onAction(ActiveWorkoutUiAction.OpenExercisePicker) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) {
                Text("Add exercise")
            }
            androidx.compose.material3.TextButton(
                onClick = { viewModel.onAction(ActiveWorkoutUiAction.Discard) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Discard workout")
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(14.dp))
        }
    }
    }
}

@Composable
private fun workoutFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.background,
    unfocusedContainerColor = MaterialTheme.colorScheme.background,
    disabledContainerColor = MaterialTheme.colorScheme.background,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
)

private fun Enum<*>.displayName(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun Double.toEditableString(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()

private fun Int.toTimerLabel(): String =
    "${this / 60}:${(this % 60).toString().padStart(2, '0')}"

private fun Int.toDurationLabel(): String {
    val hours = this / 3_600
    val minutes = (this % 3_600) / 60
    val seconds = this % 60
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "$minutes:${seconds.toString().padStart(2, '0')}"
    }
}
