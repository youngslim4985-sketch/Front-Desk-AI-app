package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CoolBlue,
    onPrimary = Color.White,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    background = SlateGray950,
    onBackground = Color(0xFFF8FAFC),
    surface = SlateGray900,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = SlateGray800,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = SlateGray700,
    error = CrimsonRed
)

private val LightColorScheme = lightColorScheme(
    primary = CoolBlue,
    onPrimary = Color.White,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = SlateGray950,
    surface = LightSurface,
    onSurface = SlateGray950,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateGray700,
    outline = LightBorder,
    error = CrimsonRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve our beautiful designer colors
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
