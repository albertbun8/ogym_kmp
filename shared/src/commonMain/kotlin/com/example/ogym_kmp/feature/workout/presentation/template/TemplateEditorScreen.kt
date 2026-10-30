package com.example.ogym_kmp.feature.workout.presentation.template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.workout.presentation.active.ExercisePickerState
import com.example.ogym_kmp.feature.workout.presentation.component.ExercisePickerDialog
import com.example.ogym_kmp.feature.workout.presentation.component.SwipeToDelete

@Composable
fun TemplateEditorScreen(
    viewModel: TemplateEditorViewModel,
    modifier: Modifier = Modifier,
    onClose: (Boolean) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editingName by remember { mutableStateOf(false) }
    var editedName by remember(state.name) { mutableStateOf(state.name) }
    var draggedExerciseId by remember { mutableStateOf<kotlin.uuid.Uuid?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    val listState = rememberLazyListState()
    val defaultViewConfiguration = LocalViewConfiguration.current
    val reorderViewConfiguration = remember(defaultViewConfiguration) {
        object : ViewConfiguration by defaultViewConfiguration {
            override val longPressTimeoutMillis: Long = 1_000L
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.events.collect { onClose(it == TemplateEditorEvent.Saved) }
    }
    if (state.pickerVisible) ExercisePickerDialog(
        state = state.picker,
        onExerciseSelected = { viewModel.onAction(TemplateEditorAction.SelectExercise(it)) },
        onRetry = { viewModel.onAction(TemplateEditorAction.OpenPicker) },
        onDismiss = { viewModel.onAction(TemplateEditorAction.ClosePicker) },
    )
    if (editingName) {
        AlertDialog(
            onDismissRequest = { editingName = false },
            title = { Text("Template name") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onAction(TemplateEditorAction.ChangeName(editedName))
                        editingName = false
                    },
                    enabled = editedName.isNotBlank(),
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { editingName = false }) { Text("Cancel") } },
        )
    }
    CompositionLocalProvider(LocalViewConfiguration provides reorderViewConfiguration) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClickLabel = "Rename template") {
                        editedName = state.name
                        editingName = true
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.name.uppercase(),
                        modifier = Modifier.weight(1f, fill = false),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = OGymTheme.colors.accent,
                    )
                }
                Text(
                    text = "${state.exercises.size} exercises · Tap to rename",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = { viewModel.onAction(TemplateEditorAction.Save) },
                enabled = !state.saving && !state.loadingTemplate,
                modifier = Modifier.height(44.dp),
            ) {
                Text(
                    text = when {
                        state.saving -> "Saving…"
                        state.isEditing -> "Save"
                        else -> "Create"
                    },
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    LazyColumn(
        modifier = Modifier.weight(1f),
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        state.error?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
        itemsIndexed(state.exercises, key = { _, exercise -> exercise.id.toString() }) { _, exercise ->
            val dragging = draggedExerciseId == exercise.id
            var menuExpanded by remember(exercise.id) { mutableStateOf(false) }
            Surface(
                modifier = Modifier
                    .zIndex(if (dragging) 1f else 0f)
                    .graphicsLayer {
                        translationY = if (dragging) dragOffset else 0f
                        scaleX = if (dragging) 1.02f else 1f
                        scaleY = if (dragging) 1.02f else 1f
                    }
                    .pointerInput(exercise.id) {
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
                                val currentItem = listState.layoutInfo.visibleItemsInfo
                                    .firstOrNull { it.key == exercise.id.toString() }
                                val draggedCenter = currentItem?.let {
                                    it.offset + it.size / 2f + dragOffset
                                }
                                val exerciseIds = state.exercises.mapTo(mutableSetOf()) { it.id.toString() }
                                val targetItem = draggedCenter?.let { center ->
                                    listState.layoutInfo.visibleItemsInfo
                                        .filter { it.key in exerciseIds && it.key != exercise.id.toString() }
                                        .minByOrNull { item ->
                                            kotlin.math.abs(center - (item.offset + item.size / 2f))
                                        }
                                }
                                val targetId = (targetItem?.key as? String)?.let(kotlin.uuid.Uuid::parse)
                                if (targetId != null &&
                                    draggedCenter in targetItem.offset.toFloat()..(targetItem.offset + targetItem.size).toFloat()
                                ) {
                                    viewModel.onAction(TemplateEditorAction.MoveExercise(exercise.id, targetId))
                                    dragOffset = 0f
                                }
                            },
                        )
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DragIndicator,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp).size(20.dp),
                            tint = MaterialTheme.colorScheme.outline,
                        )
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
                                                TemplateEditorAction.RemoveExercise(exercise.id),
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                    if (exercise.sets.isNotEmpty()) {
                        TemplateSetHeader()
                    }
                    exercise.sets.forEachIndexed { setIndex, set ->
                        val input = state.inputs[set.id] ?: TemplateSetInputUi()
                        SwipeToDelete(
                            itemKey = set.id,
                            onDelete = { viewModel.onAction(TemplateEditorAction.RemoveSet(set.id)) },
                        ) {
                            TemplateSetRow(
                                setNumber = setIndex + 1,
                                weight = input.weight,
                                reps = input.reps,
                                onWeightChange = {
                                    viewModel.onAction(TemplateEditorAction.ChangeWeight(set.id, it))
                                },
                                onRepsChange = {
                                    viewModel.onAction(TemplateEditorAction.ChangeReps(set.id, it))
                                },
                            )
                        }
                    }
                    TextButton(
                        onClick = { viewModel.onAction(TemplateEditorAction.AddSet(exercise.id)) },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = OGymTheme.colors.accent),
                    ) { TemplateAddSetButtonContent() }
                }
            }
        }
        item {
            OutlinedButton(
                onClick = { viewModel.onAction(TemplateEditorAction.OpenPicker) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Add exercise", style = MaterialTheme.typography.labelLarge)
            }
        }
        item {
            TextButton(
                onClick = { viewModel.onAction(TemplateEditorAction.Cancel) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) { Text(if (state.isEditing) "Cancel editing" else "Cancel template") }
        }
    }
    }
    }
}

@Composable
private fun TemplateAddSetButtonContent() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Text("Add set", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TemplateSetHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TemplateHeaderText("Set", Modifier.weight(0.6f))
        TemplateHeaderText("LB", Modifier.weight(1.7f))
        TemplateHeaderText("Reps", Modifier.weight(1.7f))
    }
}

@Composable
private fun TemplateSetRow(
    setNumber: Int,
    weight: String,
    reps: String,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = setNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Box(
            modifier = Modifier.weight(1.7f).padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            TemplateNumberField(
                value = weight,
                onValueChange = onWeightChange,
                keyboardType = KeyboardType.Decimal,
            )
        }
        Box(
            modifier = Modifier.weight(1.7f).padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            TemplateNumberField(
                value = reps,
                onValueChange = onRepsChange,
                keyboardType = KeyboardType.Number,
            )
        }
    }
}

@Composable
private fun TemplateNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val fieldShape = RoundedCornerShape(10.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(42.dp),
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (focused) {
                            Modifier
                                .background(MaterialTheme.colorScheme.surface, fieldShape)
                                .border(2.dp, OGymTheme.colors.accent, fieldShape)
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest, fieldShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, fieldShape)
                        },
                    )
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                innerTextField()
            }
        },
    )
}

@Composable
private fun TemplateHeaderText(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

private fun Enum<*>.displayName(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
