package com.example.journeytrack

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

val Typography = Typography()
val Shapes = Shapes()

@Composable
fun ComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun ThemeSwitch(darkTheme: Boolean, onToggle: () -> Unit) {
    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colors.primary,
        uncheckedThumbColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
        checkedTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
        uncheckedTrackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )

    Switch(
        checked = darkTheme,
        onCheckedChange = { onToggle() },
        colors = switchColors
    )
}

// Light and dark color palettes
private val LightColorPalette = lightColors(
    primary = Color.Blue,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
)

private val DarkColorPalette = darkColors(
    primary = Color.Cyan,
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ComposeTheme(darkTheme) {
        content()
    }
}
