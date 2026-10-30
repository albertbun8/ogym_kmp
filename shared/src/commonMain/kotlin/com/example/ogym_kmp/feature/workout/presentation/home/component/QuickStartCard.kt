package com.example.ogym_kmp.feature.workout.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        shape = RoundedCornerShape(22.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(Res.drawable.ogym_mascot),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
                    .size(width = 88.dp, height = 100.dp),
                contentScale = ContentScale.Fit,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.76f)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = if (proposedTemplate == null) "START TRAINING" else "NEXT WORKOUT",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnHeroMuted,
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = (proposedTemplate?.name ?: "Empty workout").uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = proposedTemplate?.let { "${it.exerciseCount} exercises · ${it.setCount} sets" }
                            ?: "Build your session as you go.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnHeroMuted,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Start workout", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
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
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(OGymTheme.colors.accent, CircleShape),
                )
                Text(
                    text = "IN PROGRESS",
                    style = MaterialTheme.typography.labelMedium,
                    color = OGymTheme.colors.accent,
                )
            }
            Text(
                text = workout.name.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Use the workout sheet below to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Secondary text on the filled crimson hero card. */
private val OnHeroMuted = Color(0xFFFFE3E6)
