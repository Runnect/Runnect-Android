package com.runnect.runnect.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val RunnectColorScheme = lightColorScheme(
    primary = M1,
    onPrimary = White,
    primaryContainer = M3,
    onPrimaryContainer = M1,
    secondary = M2,
    onSecondary = White,
    secondaryContainer = M5,
    onSecondaryContainer = M1,
    background = White,
    onBackground = G1,
    surface = White,
    onSurface = G1,
    surfaceVariant = G5,
    onSurfaceVariant = G2,
    outline = G3,
    outlineVariant = G4,
    error = Red,
    onError = White,
)

@Composable
fun RunnectTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalRunnectTextStyles provides RunnectTextStyles()
    ) {
        MaterialTheme(
            colorScheme = RunnectColorScheme,
            content = content
        )
    }
}

object RunnectTheme {
    val textStyle: RunnectTextStyles
        @Composable
        @ReadOnlyComposable
        get() = LocalRunnectTextStyles.current
}
