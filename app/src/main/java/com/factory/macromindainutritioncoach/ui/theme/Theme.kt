package com.factory.macromindainutritioncoach.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = Teal80,
    onSecondary = Teal20,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,
    tertiary = DarkGreen80,
    onTertiary = DarkGreen20,
    tertiaryContainer = DarkGreen30,
    onTertiaryContainer = DarkGreen90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey90,
    surfaceVariant = GreenGrey30,
    onSurfaceVariant = GreenGrey80,
    outline = GreenGrey60
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Green10,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Teal40,
    onSecondary = Teal10,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal10,
    tertiary = DarkGreen40,
    onTertiary = DarkGreen10,
    tertiaryContainer = DarkGreen90,
    onTertiaryContainer = DarkGreen10,
    error = Red40,
    onError = Red10,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    surfaceVariant = GreenGrey90,
    onSurfaceVariant = GreenGrey30,
    outline = GreenGrey50
)

@Composable
fun MacroMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
