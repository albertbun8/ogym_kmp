package com.example.ogym_kmp.feature.workout.presentation.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
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
            color = MaterialTheme.colorScheme.primary,
            trackColor = WorkoutProgressTrackColor,
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
                        else BorderStroke(1.dp, WorkoutDividerColor),
                    tonalElevation = 0.dp,
                    shadowElevation = if (dragging) 8.dp else 2.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
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
                                .padding(bottom = 0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                            ) {
                                Text(
                                    text = exercise.exerciseName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = exercise.muscleGroups.joinToString { it.displayName() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (dragging) {
                                Text(
                                    text = "Dragging",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            } else {
                                Box {
                                    IconButton(
                                        onClick = { menuExpanded = true },
                                        modifier = Modifier.size(32.dp),
                                    ) { Text("⋮") }
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
                        if (exercise.sets.isNotEmpty()) {
                            WorkoutSetHeader(
                                weightUnit = state.workout.preferredWeightUnit.name.lowercase(),
                            )
                        }
                        exercise.sets.forEachIndexed { setIndex, set ->
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
                                WorkoutSetRow(
                                    setNumber = setIndex + 1,
                                    previous = previous?.let {
                                        "${it.weight.toEditableString()} ${it.weightUnit.name.lowercase()} × ${it.reps}"
                                    } ?: "—",
                                    weight = input.weight,
                                    reps = input.reps,
                                    completed = set.completed,
                                    canComplete = canComplete,
                                    onWeightChange = {
                                        viewModel.onAction(ActiveWorkoutUiAction.ChangeSetWeight(set.id, it))
                                    },
                                    onRepsChange = {
                                        viewModel.onAction(ActiveWorkoutUiAction.ChangeSetReps(set.id, it))
                                    },
                                    onCompletionChange = {
                                        viewModel.onAction(
                                            ActiveWorkoutUiAction.SetSetCompleted(set.id, it),
                                        )
                                    },
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 3.dp),
                            color = WorkoutDividerColor,
                        )
                        OutlinedButton(
                            onClick = { viewModel.onAction(ActiveWorkoutUiAction.AddSet(exercise.id)) },
                            modifier = Modifier.fillMaxWidth().height(38.dp),
                            border = null,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            AddSetButtonContent()
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

private const val SetColumnWeight = 0.55f
private const val PreviousColumnWeight = 1.45f
private const val WeightColumnWeight = 1.25f
private const val RepsColumnWeight = 1f
private const val DoneColumnWeight = 0.7f
private val WorkoutFieldColor = Color(0xFFF3F4F6)
private val WorkoutDisabledFieldColor = Color(0xFFF7F7F8)
private val WorkoutDividerColor = Color(0xFFE5E7EB)
private val WorkoutProgressTrackColor = Color(0xFFE5E7EB)

@Composable
private fun AddSetButtonContent() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text("Add set", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun WorkoutSetHeader(weightUnit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SetColumn { SetHeaderText("Set") }
        PreviousColumn { SetHeaderText("Previous") }
        WeightColumn { SetHeaderText(weightUnit.uppercase()) }
        RepsColumn { SetHeaderText("Reps") }
        DoneColumn { SetHeaderText("Done") }
    }
}

@Composable
private fun WorkoutSetRow(
    setNumber: Int,
    previous: String,
    weight: String,
    reps: String,
    completed: Boolean,
    canComplete: Boolean,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompletionChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SetColumn {
            Text(
                text = setNumber.toString(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        PreviousColumn {
            Text(
                text = previous,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        WeightColumn {
            WorkoutNumberField(
                value = weight,
                onValueChange = onWeightChange,
                keyboardType = KeyboardType.Decimal,
                enabled = !completed,
            )
        }
        RepsColumn {
            WorkoutNumberField(
                value = reps,
                onValueChange = onRepsChange,
                keyboardType = KeyboardType.Number,
                enabled = !completed,
            )
        }
        DoneColumn {
            CompletionButton(
                checked = completed,
                enabled = completed || canComplete,
                onClick = { onCompletionChange(!completed) },
            )
        }
    }
}

@Composable
private fun WorkoutNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    enabled: Boolean,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(36.dp),
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (enabled) WorkoutFieldColor else WorkoutDisabledFieldColor,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                innerTextField()
            }
        },
    )
}

@Composable
private fun CompletionButton(
    checked: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = when {
        checked -> MaterialTheme.colorScheme.primary
        enabled -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    Box(
        modifier = Modifier
            .size(38.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (checked) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape,
                )
                .border(width = 2.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Text(
                    text = "✓",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SetHeaderText(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

@Composable
private fun RowScope.SetColumn(content: @Composable () -> Unit) {
    Box(Modifier.weight(SetColumnWeight), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun RowScope.PreviousColumn(content: @Composable () -> Unit) {
    Box(
        Modifier.weight(PreviousColumnWeight).padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun RowScope.WeightColumn(content: @Composable () -> Unit) {
    Box(
        Modifier.weight(WeightColumnWeight).padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun RowScope.RepsColumn(content: @Composable () -> Unit) {
    Box(
        Modifier.weight(RepsColumnWeight).padding(horizontal = 3.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun RowScope.DoneColumn(content: @Composable () -> Unit) {
    Box(Modifier.weight(DoneColumnWeight), contentAlignment = Alignment.Center) { content() }
}

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
