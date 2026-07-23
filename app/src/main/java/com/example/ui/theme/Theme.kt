package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = Color.White,
    primaryContainer = VaultSurfaceVariant,
    onPrimaryContainer = VaultTextPrimary,
    secondary = VaultSecondary,
    onSecondary = Color.White,
    tertiary = VaultTertiary,
    background = VaultDarkBg,
    onBackground = VaultTextPrimary,
    surface = VaultSurfaceDark,
    onSurface = VaultTextPrimary,
    surfaceVariant = VaultSurfaceVariant,
    onSurfaceVariant = VaultTextSecondary,
    outline = VaultCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = VaultPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF4C1D95),
    secondary = VaultSecondary,
    onSecondary = Color.White,
    tertiary = VaultTertiary,
    background = Color(0xFFF8F7FC),
    onBackground = Color(0xFF1F1A2C),
    surface = Color.White,
    onSurface = Color(0xFF1F1A2C),
    surfaceVariant = Color(0xFFF1EEF9),
    onSurfaceVariant = Color(0xFF6B6187),
    outline = Color(0xFFDDD6FE)
)

@Composable
fun PrivaDiaryTheme(
    darkTheme: Boolean = true, // Default to sleek security dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

