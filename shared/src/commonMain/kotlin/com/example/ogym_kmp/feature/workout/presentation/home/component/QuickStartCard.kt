package com.example.ogym_kmp.feature.workout.presentation.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.ogym_kmp.feature.workout.domain.model.ActiveWorkoutDraft
import com.example.ogym_kmp.feature.workout.presentation.home.WorkoutTemplateItemUi
import ogym_kmp.shared.generated.resources.Res
import ogym_kmp.shared.generated.resources.ogym_mascot
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuickStartCard(
    activeWorkout: ActiveWorkoutDraft?,
    proposedTemplate: WorkoutTemplateItemUi?,
    onStartClick: () -> Unit,
) {
    if (activeWorkout != null) {
        InProgressWorkoutCard(activeWorkout)
        return
    }

    Card(
        onClick = onStartClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(164.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.ogym_mascot),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 18.dp)
                    .size(width = 150.dp, height = 174.dp),
                contentScale = ContentScale.Fit,
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.67f)
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    if (proposedTemplate == null) "START TRAINING" else "NEXT WORKOUT",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        proposedTemplate?.name ?: "Empty workout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                    )
                    Text(
                        proposedTemplate?.let { "${it.exerciseCount} exercises  ·  ${it.setCount} sets" }
                            ?: "Build your session as you go.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    modifier = Modifier.size(width = 92.dp, height = 34.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Start  →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InProgressWorkoutCard(workout: ActiveWorkoutDraft) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "IN PROGRESS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Use the workout sheet below to continue.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
