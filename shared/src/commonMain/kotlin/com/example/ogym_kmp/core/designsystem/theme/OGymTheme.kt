package com.example.ogym_kmp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = CrimsonLight,
    onPrimary = Color(0xFF680019),
    primaryContainer = CrimsonContainerDark,
    onPrimaryContainer = Color(0xFFFFD9DE),
    background = Ink,
    onBackground = Color(0xFFE1E4DF),
    surface = DarkSurface,
    onSurface = Color(0xFFE1E4DF),
    surfaceContainer = DarkSurfaceHigh,
    onSurfaceVariant = Color(0xFFBEC9BE),
    outlineVariant = Color(0xFF3F4940),
    error = Color(0xFFFFB4AB),
)

private val LightColors = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = CrimsonContainerLight,
    onPrimaryContainer = Color(0xFF3F0010),
    background = LightBackground,
    onBackground = Color(0xFF191C19),
    surface = LightSurface,
    onSurface = Color(0xFF191C19),
    surfaceContainer = LightSurfaceHigh,
    onSurfaceVariant = Color(0xFF424940),
    outlineVariant = Color(0xFFC2C9BF),
)

@Composable
fun OGymTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
