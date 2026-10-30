package com.example.ogym_kmp.feature.workout.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ogym_kmp.core.common.currentWeekDates
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme

@Composable
fun WeeklyActivityCalendar(
    completedDates: Set<String>,
    modifier: Modifier = Modifier,
) {
    val weekDates = remember { currentWeekDates() }
    val dayShape = RoundedCornerShape(10.dp)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        weekDates.forEach { date ->
            val completed = date.isoDate in completedDates
            val description = buildString {
                append("${date.fullDayLabel} ${date.dayNumber}")
                if (completed) append(", workout completed")
                if (date.isToday) append(", today")
            }
            val dayModifier = when {
                completed -> Modifier.background(MaterialTheme.colorScheme.primary, dayShape)
                date.isToday -> Modifier.border(2.dp, OGymTheme.colors.accent, dayShape)
                else -> Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest, dayShape)
            }
            val numberColor = when {
                completed -> MaterialTheme.colorScheme.onPrimary
                date.isToday -> OGymTheme.colors.accent
                else -> MaterialTheme.colorScheme.onSurface
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clearAndSetSemantics { contentDescription = description },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = date.shortDayLabel.take(1),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (date.isToday) OGymTheme.colors.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .then(dayModifier),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayNumber.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = numberColor,
                        fontWeight = if (completed || date.isToday) FontWeight.Bold else FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
