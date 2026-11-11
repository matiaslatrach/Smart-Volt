package com.example.smartvolt.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AzulOscuro,
    onPrimary = Color.White,
    secondary = VerdeVolt,
    onSecondary = AzulOscuro,
    tertiary = AzulMedio,
    background = AzulOscuro,
    surface = AzulMedio,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AzulOscuro,
    onPrimary = Color.White,
    secondary = VerdeVolt,
    onSecondary = AzulOscuro,
    tertiary = AzulMedio,
    background = Fondo,
    surface = Blanco,
    onBackground = AzulOscuro,
    onSurface = AzulOscuro,
    outline = Borde,
    onSurfaceVariant = TextoSecundario
)

@Composable
fun SmartVoltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
