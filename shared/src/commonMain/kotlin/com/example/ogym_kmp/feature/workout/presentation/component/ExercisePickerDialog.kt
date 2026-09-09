package com.example.ogym_kmp.feature.workout.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ogym_kmp.feature.exercise.domain.model.Exercise
import com.example.ogym_kmp.feature.exercise.domain.model.MuscleGroup
import com.example.ogym_kmp.feature.workout.presentation.active.ExercisePickerState

@Composable
fun ExercisePickerDialog(
    state: ExercisePickerState,
    onExerciseSelected: (Exercise) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add exercise") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search exercises") },
                    singleLine = true,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedMuscleGroup == null,
                            onClick = { selectedMuscleGroup = null },
                            label = { Text("All") },
                        )
                    }
                    items(MuscleGroup.entries) { muscleGroup ->
                        FilterChip(
                            selected = selectedMuscleGroup == muscleGroup,
                            onClick = {
                                selectedMuscleGroup = muscleGroup.takeUnless { it == selectedMuscleGroup }
                            },
                            label = { Text(muscleGroup.displayName()) },
                        )
                    }
                }
                when (state) {
                    ExercisePickerState.Idle,
                    ExercisePickerState.Loading -> Box(
                        Modifier.fillMaxWidth().height(160.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }

                    is ExercisePickerState.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        OutlinedButton(onClick = onRetry) { Text("Retry") }
                    }

                    is ExercisePickerState.Data -> {
                        val filteredExercises = state.exercises.filter { exercise ->
                            exercise.name.contains(query.trim(), ignoreCase = true) &&
                                (selectedMuscleGroup == null || selectedMuscleGroup in exercise.muscleGroups)
                        }
                        if (filteredExercises.isEmpty()) {
                            Text("No exercises match these filters.")
                        } else {
                            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 360.dp)) {
                                items(filteredExercises, key = { it.id.toString() }) { exercise ->
                                    Column(
                                        Modifier.fillMaxWidth()
                                            .clickable { onExerciseSelected(exercise) }
                                            .padding(vertical = 10.dp),
                                    ) {
                                        Text(exercise.name, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            exercise.muscleGroups.joinToString { it.displayName() },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun MuscleGroup.displayName(): String =
    name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
