package com.example.ogym_kmp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ogym_kmp.core.designsystem.theme.OGymTheme

@Composable
fun OGymTopBar(
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 20.dp),
    ) {
        Box(Modifier.align(Alignment.CenterStart)) {
            OGymWordmark()
        }
        Surface(
            onClick = onNotificationsClick,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(44.dp)
                .semantics { contentDescription = "Notifications" },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(contentAlignment = Alignment.Center) {
                NotificationBell()
            }
        }
    }
}

@Composable
private fun OGymWordmark() {
    val logoColor = OGymTheme.colors.accent
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(width = 36.dp, height = 24.dp)) {
            val barY = size.height * 0.28f
            val barStroke = size.height * 0.08f
            val bodyStroke = size.height * 0.11f

            // Bar and weight plates.
            drawLine(
                color = logoColor,
                start = Offset(size.width * 0.04f, barY),
                end = Offset(size.width * 0.96f, barY),
                strokeWidth = barStroke,
                cap = StrokeCap.Square,
            )
            drawRect(
                color = logoColor,
                topLeft = Offset(size.width * 0.08f, size.height * 0.08f),
                size = Size(size.width * 0.07f, size.height * 0.4f),
            )
            drawRect(
                color = logoColor,
                topLeft = Offset(size.width * 0.17f, size.height * 0.02f),
                size = Size(size.width * 0.09f, size.height * 0.52f),
            )
            drawRect(
                color = logoColor,
                topLeft = Offset(size.width * 0.74f, size.height * 0.02f),
                size = Size(size.width * 0.09f, size.height * 0.52f),
            )
            drawRect(
                color = logoColor,
                topLeft = Offset(size.width * 0.85f, size.height * 0.08f),
                size = Size(size.width * 0.07f, size.height * 0.4f),
            )

            // Arms reach from the shoulders to the bar grips.
            drawLine(
                color = logoColor,
                start = Offset(size.width * 0.38f, size.height * 0.6f),
                end = Offset(size.width * 0.31f, barY),
                strokeWidth = bodyStroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = logoColor,
                start = Offset(size.width * 0.62f, size.height * 0.6f),
                end = Offset(size.width * 0.69f, barY),
                strokeWidth = bodyStroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = logoColor,
                start = Offset(size.width * 0.38f, size.height * 0.6f),
                end = Offset(size.width * 0.62f, size.height * 0.6f),
                strokeWidth = bodyStroke,
                cap = StrokeCap.Round,
            )

            // Head and torso make the lifter readable at header size.
            drawCircle(
                color = logoColor,
                radius = size.height * 0.105f,
                center = Offset(size.width * 0.5f, size.height * 0.48f),
            )
            drawRect(
                color = logoColor,
                topLeft = Offset(size.width * 0.42f, size.height * 0.65f),
                size = Size(size.width * 0.16f, size.height * 0.32f),
            )
        }
        Text(
            text = "OGYM",
            color = logoColor,
            style = MaterialTheme.typography.headlineSmall,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun NotificationBell() {
    val bellColor = MaterialTheme.colorScheme.onBackground
    Canvas(Modifier.size(20.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = bellColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.25f, size.height * 0.14f),
            size = Size(size.width * 0.5f, size.height * 0.58f),
            style = stroke,
        )
        drawLine(
            color = bellColor,
            start = Offset(size.width * 0.25f, size.height * 0.43f),
            end = Offset(size.width * 0.2f, size.height * 0.72f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = bellColor,
            start = Offset(size.width * 0.75f, size.height * 0.43f),
            end = Offset(size.width * 0.8f, size.height * 0.72f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = bellColor,
            start = Offset(size.width * 0.2f, size.height * 0.72f),
            end = Offset(size.width * 0.8f, size.height * 0.72f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = bellColor,
            radius = size.width * 0.06f,
            center = Offset(size.width * 0.5f, size.height * 0.84f),
        )
    }
}
