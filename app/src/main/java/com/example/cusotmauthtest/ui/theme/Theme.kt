package com.example.cusotmauthtest.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SocioDarkColorScheme = darkColorScheme(
    primary = SocioYellow,
    onPrimary = Color.Black,
    primaryContainer = SocioYellow,
    onPrimaryContainer = Color.Black,
    secondary = SocioYellowDark,
    onSecondary = Color.Black,
    background = SocioBackground,
    onBackground = SocioTextPrimary,
    surface = SocioSurface,
    onSurface = SocioTextPrimary,
    surfaceVariant = SocioSurfaceVariant,
    onSurfaceVariant = SocioTextSecondary,
    outline = SocioCardBorder,
    error = SocioCallRed,
    onError = Color.White
)

@Composable
fun CusotmAuthTestTheme(
    darkTheme: Boolean = true, // Force dark theme to match design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = SocioDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SocioBackground.toArgb()
                window.navigationBarColor = SocioBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

