package com.example.korokoai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C5CE7),
    secondary = Color(0xFFA29BFE),
    tertiary = Color(0xFF74B9FF),
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF2D2D2D)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6C5CE7),
    secondary = Color(0xFFA29BFE),
    tertiary = Color(0xFF74B9FF),
    background = Color(0xFFF5F5F5),
    surface = Color.White
)

@Composable
fun KorokoAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
