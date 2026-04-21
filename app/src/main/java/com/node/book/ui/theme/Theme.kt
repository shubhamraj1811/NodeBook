package com.node.book.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Light Color Scheme ───────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = AppYellow,
    onPrimary        = LightOnBackground,
    secondary        = AppOrange,
    onSecondary      = LightOnBackground,
    background       = LightBackground,
    onBackground     = LightOnBackground,
    surface          = LightSurface,
    onSurface        = LightOnSurface,
    surfaceVariant   = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error            = DeleteRed,
)

// ─── Dark Color Scheme ────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary          = AppYellow,
    onPrimary        = DarkOnBackground,
    secondary        = AppOrange,
    onSecondary      = DarkOnBackground,
    background       = DarkBackground,
    onBackground     = DarkOnBackground,
    surface          = DarkSurface,
    onSurface        = DarkOnSurface,
    surfaceVariant   = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error            = DeleteRed,
)

@Composable
fun NotesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // follows system by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // ─── Make status bar match our theme ──────────────────
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}