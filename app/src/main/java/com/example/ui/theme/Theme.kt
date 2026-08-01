package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = Color(0xFF0F172A),
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
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color.White,
    tertiary = Color(0xFF10B981),
    background = Color(0xFFF0F9FF),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE0F2FE),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFBAE6FD)
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

