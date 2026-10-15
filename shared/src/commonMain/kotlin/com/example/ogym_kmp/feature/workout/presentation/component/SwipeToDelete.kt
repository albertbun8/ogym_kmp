package com.example.ogym_kmp.feature.workout.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDelete(
    itemKey: Any,
    onDelete: () -> Unit,
    content: @Composable () -> Unit,
) {
    key(itemKey) {
        val dismissState = rememberSwipeToDismissBoxState()
        var deleteRequested by remember { mutableStateOf(false) }

        LaunchedEffect(dismissState.currentValue) {
            if (
                dismissState.currentValue == SwipeToDismissBoxValue.EndToStart &&
                !deleteRequested
            ) {
                deleteRequested = true
                onDelete()
                dismissState.reset()
                deleteRequested = false
            }
        }

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer),
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    content()
                }
            },
        )
    }
}
