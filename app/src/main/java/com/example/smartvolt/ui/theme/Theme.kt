
package com.example.smartvolt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VerdeVolt,
    onPrimary = BosqueProfundo,
    secondary = VerdePrincipal,
    onSecondary = Color.White,
    tertiary = EstadoInformativo,
    background = BosqueProfundo,
    onBackground = Color.White,
    surface = Color(0xFF1C3834),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF29443E),
    onSurfaceVariant = Color(0xFFD0DED5),
    outline = Color(0xFF526C61),
    error = EstadoError
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color.White,
    secondary = VerdeVolt,
    onSecondary = BosqueProfundo,
    tertiary = EstadoInformativo,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Blanco,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieSuave,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    error = EstadoError
)

@Composable
fun SmartVoltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
