package com.example.ogym_kmp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = SoftCrimson,
    onPrimaryContainer = DeepCrimson,
    secondary = DeepCrimson,
    onSecondary = Color.White,
    secondaryContainer = DeepCrimson,
    onSecondaryContainer = Color.White,
    tertiary = AchievementAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE8CC),
    onTertiaryContainer = Color(0xFF5C2D00),
    background = NeutralCanvas,
    onBackground = CharcoalInk,
    surface = CleanSurface,
    onSurface = CharcoalInk,
    surfaceVariant = SoftCrimson,
    surfaceContainer = CleanSurface,
    surfaceContainerLowest = CleanSurface,
    surfaceContainerLow = CleanSurface,
    surfaceContainerHigh = NeutralCanvas,
    surfaceContainerHighest = WarmSurfaceStrong,
    surfaceDim = WarmSurfaceStrong,
    surfaceBright = CleanSurface,
    onSurfaceVariant = MutedRoseInk,
    surfaceTint = Crimson,
    inverseSurface = CharcoalInk,
    inverseOnSurface = NeutralCanvas,
    inversePrimary = Color(0xFFFFB2BC),
    outline = Color(0xFFD6C5C8),
    outlineVariant = RoseOutline,
    scrim = CharcoalInk,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val DarkColors = darkColorScheme(
    primary = EmberCrimson,
    onPrimary = Color.White,
    primaryContainer = EmberCrimsonContainer,
    onPrimaryContainer = Color(0xFFFFD9DD),
    secondary = EmberCrimsonStrong,
    onSecondary = Color.White,
    secondaryContainer = EmberCrimsonContainer,
    onSecondaryContainer = Color(0xFFFFD9DD),
    tertiary = Color(0xFFFFB86A),
    onTertiary = Color(0xFF4A2700),
    tertiaryContainer = Color(0xFF673A00),
    onTertiaryContainer = Color(0xFFFFDCC0),
    background = DarkCanvas,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceStrong,
    surfaceContainer = DarkSurface,
    surfaceContainerLowest = DarkCanvas,
    surfaceContainerLow = DarkSurface,
    surfaceContainerHigh = DarkSurfaceStrong,
    surfaceContainerHighest = Color(0xFF352D2F),
    surfaceDim = DarkCanvas,
    surfaceBright = DarkSurfaceStrong,
    onSurfaceVariant = DarkMutedInk,
    surfaceTint = EmberCrimson,
    inverseSurface = DarkInk,
    inverseOnSurface = CharcoalInk,
    inversePrimary = Crimson,
    outline = Color(0xFF817B7D),
    outlineVariant = DarkOutline,
    scrim = Color.Black,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = OGymTypography,
        shapes = OGymShapes,
        content = content,
    )
}
