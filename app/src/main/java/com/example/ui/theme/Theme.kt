package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MotionCraftDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = PrimaryPurpleLight,
    secondary = SecondaryBlue,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = AccentCyan,
    tertiary = AccentCyan,
    background = BgDeepDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MotionCraftTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MotionCraftDarkColorScheme,
        typography = Typography,
        content = content
    )
}
