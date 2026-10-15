package com.example.ogym_kmp.feature.workout.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ogym_kmp.core.common.currentWeekDates

@Composable
fun WeeklyActivityCalendar(
    completedDates: Set<String>,
    modifier: Modifier = Modifier,
) {
    val weekDates = remember { currentWeekDates() }

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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = description },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = date.shortDayLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = if (completed) MaterialTheme.colorScheme.secondary else Color.Transparent,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayNumber.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (completed) {
                            MaterialTheme.colorScheme.onSecondary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        fontWeight = if (completed) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(
                            color = if (date.isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}
