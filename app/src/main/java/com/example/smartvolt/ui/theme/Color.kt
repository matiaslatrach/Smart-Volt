
package com.example.smartvolt.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable

// IDENTIDAD SMARTVOLT

val BosqueProfundo = Color(0xFF142B2A)
val VerdePrincipal = Color(0xFF1E5147)
val VerdeVolt = Color(0xFFC7E56A)

// FONDOS Y SUPERFICIES

val Fondo = Color(0xFFF4F6F2)
val Blanco = Color(0xFFFFFFFF)
val SuperficieSuave = Color(0xFFE9EFE9)

// TEXTOS

val TextoPrincipal = Color(0xFF172522)
val TextoSecundario = Color(0xFF718078)
val TextoHeaderSubtle = Color(0xFFB8C8BE)
val TextoHeaderClaro = Color(0xFFD0DED5)

// BORDES

val Borde = Color(0xFFDCE5DD)

// COLORES DE ESTADO

val EstadoExitoso = Color(0xFF2E7D5B)
val EstadoAdvertencia = Color(0xFFB7791F)
val EstadoError = Color(0xFFC64B4B)
val EstadoInformativo = Color(0xFF397A8A)

// Fondos de mensajes compartidos por los formularios.
val FondoExito = Color(0xFFEBFBEE)
val FondoError = Color(0xFFFFEEEE)

// COMPATIBILIDAD CON PANTALLAS EXISTENTES

val AzulOscuro = BosqueProfundo
val AzulMedio = VerdePrincipal

// COLORES ORIGINALES DE MATERIAL (COMPATIBILIDAD)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

/** Contraste consistente en los formularios, incluido login con modo oscuro activo. */
@Composable
fun smartVoltTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextoPrincipal,
    unfocusedTextColor = TextoPrincipal,
    disabledTextColor = TextoSecundario,
    focusedContainerColor = Blanco,
    unfocusedContainerColor = Blanco,
    disabledContainerColor = Blanco,
    focusedBorderColor = VerdePrincipal,
    unfocusedBorderColor = Borde,
    disabledBorderColor = Borde,
    cursorColor = VerdePrincipal,
    focusedLabelColor = VerdePrincipal,
    unfocusedLabelColor = TextoSecundario,
    disabledLabelColor = TextoSecundario,
    focusedPlaceholderColor = TextoSecundario,
    unfocusedPlaceholderColor = TextoSecundario,
    disabledPlaceholderColor = TextoSecundario
)
