package com.luisamsampaio.jiggie.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = AccentColor,
    background = BackgroundColor,
    surface = CardColor,
    surfaceVariant = InputBackgroundColor,
    onPrimary = Color.White,
    onSecondary = ForegroundColor,
    onBackground = ForegroundColor,
    onSurface = ForegroundColor,
    onSurfaceVariant = MutedForegroundColor,
    error = DestructiveColor,
    onError = Color.White
)

@Composable
fun JiggieTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}