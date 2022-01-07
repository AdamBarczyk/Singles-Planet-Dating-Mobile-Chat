package com.example.singlesplanetchat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PinkLight,
    primaryVariant = PinkDark,
    secondary = PurpleDark,
    secondaryVariant = PurpleLight,
    background = Background
)

private val LightColorPalette = lightColors(
    primary = PinkLight,
    primaryVariant = PinkDark,
    secondary = PurpleDark,
    secondaryVariant = PurpleLight,
    background = Background
)

@Composable
fun SinglesPlanetTheme(
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