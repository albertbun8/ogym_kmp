package com.example.ogym_kmp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ogym_kmp.shared.generated.resources.Res
import ogym_kmp.shared.generated.resources.barlow_condensed_bold
import ogym_kmp.shared.generated.resources.barlow_condensed_extrabold
import ogym_kmp.shared.generated.resources.barlow_condensed_semibold
import org.jetbrains.compose.resources.Font

private val LightColors = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = SoftCrimson,
    onPrimaryContainer = DeepCrimson,
    secondary = DeepCrimson,
    onSecondary = Color.White,
    secondaryContainer = SoftCrimson,
    onSecondaryContainer = DeepCrimson,
    tertiary = AchievementAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE8CC),
    onTertiaryContainer = Color(0xFF5C2D00),
    background = WarmCanvas,
    onBackground = CharcoalInk,
    surface = CleanSurface,
    onSurface = CharcoalInk,
    surfaceVariant = WarmCanvas,
    surfaceContainer = CleanSurface,
    surfaceContainerLowest = CleanSurface,
    surfaceContainerLow = CleanSurface,
    surfaceContainerHigh = CleanSurface,
    surfaceContainerHighest = WarmCanvas,
    surfaceDim = WarmCanvas,
    surfaceBright = CleanSurface,
    onSurfaceVariant = MutedRoseInk,
    surfaceTint = Crimson,
    inverseSurface = CharcoalInk,
    inverseOnSurface = WarmCanvas,
    inversePrimary = Color(0xFFFFB2BC),
    outline = RoseOutlineStrong,
    outlineVariant = RoseOutline,
    scrim = CharcoalInk,
    error = Color(0xFFA61B2B),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val DarkColors = darkColorScheme(
    primary = EmberCrimson,
    onPrimary = Color.White,
    primaryContainer = EmberCrimsonContainer,
    onPrimaryContainer = EmberCrimsonOnContainer,
    secondary = EmberCrimsonText,
    onSecondary = Color(0xFF3B0A11),
    secondaryContainer = EmberCrimsonContainer,
    onSecondaryContainer = EmberCrimsonOnContainer,
    tertiary = Color(0xFFF5A524),
    onTertiary = Color(0xFF4A2700),
    tertiaryContainer = Color(0xFF3A2A1A),
    onTertiaryContainer = Color(0xFFF5B54A),
    background = DarkCanvas,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceStrong,
    surfaceContainer = DarkSurface,
    surfaceContainerLowest = DarkCanvas,
    surfaceContainerLow = DarkSurface,
    surfaceContainerHigh = DarkSurface,
    surfaceContainerHighest = DarkSurfaceStrong,
    surfaceDim = DarkCanvas,
    surfaceBright = DarkSurfaceStrong,
    onSurfaceVariant = DarkMutedInk,
    surfaceTint = EmberCrimson,
    inverseSurface = DarkInk,
    inverseOnSurface = CharcoalInk,
    inversePrimary = Crimson,
    outline = DarkOutlineStrong,
    outlineVariant = DarkOutline,
    scrim = Color.Black,
    error = Color(0xFFFF8A96),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

/**
 * Colors the Material scheme has no role for.
 *
 * In dark mode the crimson used for filled surfaces (`primary`, white text on it)
 * is too dark to be read as text on the dark background, so text and icons use
 * a lighter crimson: [accent].
 */
@Immutable
data class OGymExtendedColors(
    val accent: Color,
    val achievement: Color,
    val floatingBar: Color,
    val onFloatingBar: Color,
    val floatingBarSelected: Color,
    val onFloatingBarSelected: Color,
)

private val LightExtendedColors = OGymExtendedColors(
    accent = Crimson,
    achievement = Color(0xFFB45309),
    floatingBar = CharcoalInk,
    onFloatingBar = Color(0xFFC9BFC1),
    floatingBarSelected = CleanSurface,
    onFloatingBarSelected = CharcoalInk,
)

private val DarkExtendedColors = OGymExtendedColors(
    accent = EmberCrimsonText,
    achievement = Color(0xFFF5B54A),
    floatingBar = Color(0xFF1C1B1D),
    onFloatingBar = DarkMutedInk,
    floatingBarSelected = DarkInk,
    onFloatingBarSelected = DarkCanvas,
)

private val LocalOGymColors = staticCompositionLocalOf { LightExtendedColors }

object OGymTheme {
    val colors: OGymExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOGymColors.current
}

@Composable
private fun barlowCondensed() = FontFamily(
    Font(Res.font.barlow_condensed_semibold, FontWeight.SemiBold),
    Font(Res.font.barlow_condensed_bold, FontWeight.Bold),
    Font(Res.font.barlow_condensed_extrabold, FontWeight.ExtraBold),
)

@Composable
private fun ogymTypography(): Typography {
    val display = barlowCondensed()
    return Typography(
        displaySmall = TextStyle(
            fontFamily = display,
            fontSize = 40.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = display,
            fontSize = 36.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.ExtraBold,
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.ExtraBold,
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontSize = 24.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
        ),
        titleMedium = TextStyle(
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        titleSmall = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
        bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
        labelLarge = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
        ),
    )
}

private val OGymShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(22.dp),
)

@Composable
fun OGymTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalOGymColors provides if (darkTheme) DarkExtendedColors else LightExtendedColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = ogymTypography(),
            shapes = OGymShapes,
            content = content,
        )
    }
}
