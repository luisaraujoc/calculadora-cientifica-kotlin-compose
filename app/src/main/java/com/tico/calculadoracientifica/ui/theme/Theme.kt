package com.tico.calculadoracientifica.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Sunglow,        // tom vibrante para destacar
    onPrimary = SmokyBlack,   // contraste sobre o primary
    secondary = Tomato,
    onSecondary = LavanderBlush,
    background = SmokyBlack,
    onBackground = LavanderBlush,
    surface = SmokyBlack,
    onSurface = LavanderBlush,
    tertiary = Celadon
)

private val LightColorScheme = lightColorScheme(
    primary = Sunglow,
    onPrimary = SmokyBlack,
    secondary = Tomato,
    onSecondary = SmokyBlack,
    background = LavanderBlush,
    onBackground = SmokyBlack,
    surface = LavanderBlush,
    onSurface = SmokyBlack,
    tertiary = YaleBlue
)

@Composable
fun CalculadoracientificaTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
