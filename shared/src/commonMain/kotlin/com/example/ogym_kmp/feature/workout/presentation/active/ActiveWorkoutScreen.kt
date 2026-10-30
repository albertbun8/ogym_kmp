package com.example.ogym_kmp.feature.workout.presentation.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
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
    contentPadding: PaddingValues = PaddingValues(),
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
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = state.workout.name.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkoutChip(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(OGymTheme.colors.accent, CircleShape),
                        )
                        Text(
                            text = state.elapsedWorkoutSeconds.toDurationLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    WorkoutChip(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Rest ${state.remainingRestSeconds.toTimerLabel()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Button(
                onClick = { viewModel.onAction(ActiveWorkoutUiAction.Finish) },
                enabled = !state.isFinishing,
                modifier = Modifier.height(44.dp),
            ) {
                Text(
                    text = if (state.isFinishing) "Finishing…" else "Finish",
                    style = MaterialTheme.typography.labelLarge,
                )
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${state.workout.exercises.size} exercises",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "$completedSetCount / $totalSetCount sets",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                )
            }

        LinearProgressIndicator(
            progress = { workoutProgress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = OGymTheme.colors.accent,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            gapSize = 0.dp,
            drawStopIndicator = {},
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
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    border = if (dragging) BorderStroke(2.dp, OGymTheme.colors.accent) else null,
                    tonalElevation = 0.dp,
                    shadowElevation = if (dragging) 8.dp else 0.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
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
                                    text = exercise.exerciseName.uppercase(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                    color = OGymTheme.colors.accent,
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
                                    color = OGymTheme.colors.accent,
                                )
                            } else {
                                Box {
                                    IconButton(onClick = { menuExpanded = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "Exercise options",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
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
                        TextButton(
                            onClick = { viewModel.onAction(ActiveWorkoutUiAction.AddSet(exercise.id)) },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = OGymTheme.colors.accent),
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
            OutlinedButton(
                onClick = { viewModel.onAction(ActiveWorkoutUiAction.OpenExercisePicker) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Add exercise", style = MaterialTheme.typography.labelLarge)
            }
            TextButton(
                onClick = { viewModel.onAction(ActiveWorkoutUiAction.Discard) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Discard workout")
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(14.dp + contentPadding.calculateBottomPadding()))
        }
    }
    }
}

private const val SetColumnWeight = 0.55f
private const val PreviousColumnWeight = 1.45f
private const val WeightColumnWeight = 1.25f
private const val RepsColumnWeight = 1f
private const val DoneColumnWeight = 0.7f

@Composable
private fun WorkoutChip(
    containerColor: Color,
    contentColor: Color,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier
                .height(28.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
private fun AddSetButtonContent() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Text("Add set", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
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
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (completed) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(vertical = 4.dp),
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
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val fieldShape = RoundedCornerShape(10.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(40.dp),
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(OGymTheme.colors.accent),
        decorationBox = { innerTextField ->
            val fieldModifier = when {
                !enabled -> Modifier
                focused -> Modifier
                    .background(MaterialTheme.colorScheme.surface, fieldShape)
                    .border(2.dp, OGymTheme.colors.accent, fieldShape)
                else -> Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest, fieldShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, fieldShape)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(fieldModifier)
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
    val buttonShape = RoundedCornerShape(12.dp)
    val uncheckedColor = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = if (checked) "Set done" else "Mark set as done" },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .then(
                    if (checked) {
                        Modifier.background(MaterialTheme.colorScheme.primary, buttonShape)
                    } else {
                        Modifier.border(2.dp, uncheckedColor, buttonShape)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (checked) MaterialTheme.colorScheme.onPrimary else uncheckedColor,
            )
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
