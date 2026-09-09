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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.name,
                    modifier = Modifier.clickable {
                        editedName = state.name
                        editingName = true
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${state.exercises.size} exercises · Tap to rename",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = { viewModel.onAction(TemplateEditorAction.Save) },
                enabled = !state.saving,
            ) { Text(if (state.saving) "Creating…" else "Create") }
        }
    LazyColumn(
        modifier = Modifier.weight(1f),
        state = listState,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
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
                color = if (dragging) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                contentColor = if (dragging) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                border = if (dragging) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                },
                tonalElevation = 0.dp,
                shadowElevation = if (dragging) 8.dp else 0.dp,
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(exercise.exerciseName, fontWeight = FontWeight.Bold)
                        if (dragging) {
                            Text(
                                text = "Dragging",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        } else {
                            Box {
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
                                                TemplateEditorAction.RemoveExercise(exercise.id),
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                    exercise.sets.forEach { set ->
                        val input = state.inputs[set.id] ?: TemplateSetInputUi()
                        SwipeToDelete(
                            itemKey = set.id,
                            onDelete = { viewModel.onAction(TemplateEditorAction.RemoveSet(set.id)) },
                        ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${set.positionInExercise + 1}")
                            OutlinedTextField(
                                input.weight,
                                { viewModel.onAction(TemplateEditorAction.ChangeWeight(set.id, it)) },
                                Modifier.width(100.dp), label = { Text("lb") }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                ),
                            )
                            OutlinedTextField(
                                input.reps,
                                { viewModel.onAction(TemplateEditorAction.ChangeReps(set.id, it)) },
                                Modifier.width(100.dp), label = { Text("Reps") }, singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                ),
                            )
                        }
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.onAction(TemplateEditorAction.AddSet(exercise.id)) },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        border = null,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) { Text("Add set") }
                }
            }
        }
        item {
            Button(
                onClick = { viewModel.onAction(TemplateEditorAction.OpenPicker) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) { Text("Add exercise") }
        }
        item {
            TextButton(
                onClick = { viewModel.onAction(TemplateEditorAction.Cancel) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) { Text("Cancel template") }
        }
    }
    }
    }
}
