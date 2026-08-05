package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FinoraLightColorScheme = lightColorScheme(
    primary = PrimaryFinora,
    onPrimary = OnPrimaryFinora,
    primaryContainer = SecondaryFinora.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryFinora,
    secondary = SecondaryFinora,
    onSecondary = OnSecondaryFinora,
    secondaryContainer = SecondaryFinora.copy(alpha = 0.15f),
    onSecondaryContainer = PrimaryFinora,
    tertiary = AccentFinora,
    onTertiary = Color.White,
    background = BackgroundFinora,
    onBackground = OnBackgroundFinora,
    surface = SurfaceFinora,
    onSurface = OnSurfaceFinora,
    surfaceVariant = SurfaceVariantFinora,
    onSurfaceVariant = OnSurfaceFinora,
    outline = CardBorderFinora
)

private val FinoraDarkColorScheme = darkColorScheme(
    primary = Color(0xFFE8B2A0),
    onPrimary = Color(0xFF3B1817),
    primaryContainer = PrimaryFinora,
    onPrimaryContainer = Color(0xFFFFDAD5),
    secondary = SecondaryFinora,
    onSecondary = Color(0xFF381B27),
    secondaryContainer = Color(0xFF53313E),
    onSecondaryContainer = Color(0xFFFFD9E4),
    tertiary = AccentFinora,
    onTertiary = Color.White,
    background = Color(0xFF181212),
    onBackground = Color(0xFFEDE0DE),
    surface = Color(0xFF221A1A),
    onSurface = Color(0xFFEDE0DE),
    surfaceVariant = Color(0xFF382C2C),
    onSurfaceVariant = Color(0xFFD6C2BF),
    outline = Color(0xFF524341)
)

@Composable
fun FinoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Finora brand colors by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FinoraDarkColorScheme
        else -> FinoraLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias for template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FinoraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
