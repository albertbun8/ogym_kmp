package com.example.ogym_kmp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    primaryContainer = IceBackground,
    onPrimaryContainer = Violet,
    secondary = WarmGold,
    onSecondary = MidnightInk,
    secondaryContainer = WarmGold,
    onSecondaryContainer = MidnightInk,
    tertiary = Violet,
    onTertiary = Color.White,
    tertiaryContainer = IceBackground,
    onTertiaryContainer = MidnightInk,
    background = IceBackground,
    onBackground = MidnightInk,
    surface = IceSurface,
    onSurface = MidnightInk,
    surfaceVariant = IceBackground,
    surfaceContainer = IceSurface,
    surfaceContainerLowest = IceSurface,
    surfaceContainerLow = IceSurface,
    surfaceContainerHigh = IceBackground,
    surfaceContainerHighest = IceSurfaceStrong,
    surfaceDim = IceSurfaceStrong,
    surfaceBright = IceSurface,
    onSurfaceVariant = Color(0xFF6F7893),
    surfaceTint = Violet,
    inverseSurface = MidnightInk,
    inverseOnSurface = IceBackground,
    inversePrimary = Color(0xFFB9ABFF),
    outline = Color(0xFFC6CDDA),
    outlineVariant = IceSurfaceStrong,
    scrim = MidnightInk,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val OGymTypography = Typography(
    headlineMedium = TextStyle(
        fontSize = 30.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.6).sp,
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.35).sp,
    ),
    titleLarge = TextStyle(
        fontSize = 21.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Bold,
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
    labelMedium = TextStyle(
        fontSize = 11.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.7.sp,
    ),
)

private val OGymShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun OGymTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = OGymTypography,
        shapes = OGymShapes,
        content = content,
    )
}
