
package com.example.smartvolt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

data class OpcionNavegacion(
    val etiqueta: String,
    val simbolo: String
)

// Identidad visual SmartVolt
private val BosqueProfundo = Color(0xFF142B2A)
private val VerdePrincipal = Color(0xFF1E5147)
private val VerdeVolt = Color(0xFFC7E56A)
private val TextoInactivo = Color(0xFFB8C8BE)

private fun obtenerIcono(etiqueta: String): ImageVector {
    return when (etiqueta.lowercase()) {
        "inicio", "panel" -> Icons.Filled.Home

        "asistencia", "marcar" -> Icons.Filled.CheckCircle

        "usuarios", "equipo", "personal" -> Icons.Filled.People

        "perfil", "mi perfil", "cuenta" -> Icons.Filled.Person

        "solicitudes", "notificaciones" -> Icons.Filled.CheckCircle

        "configuración", "configuracion", "ajustes" ->
            Icons.Filled.Settings

        else -> Icons.Filled.Home
    }
}

@Composable
fun NavegacionInferior(
    opciones: List<OpcionNavegacion>,
    indiceSeleccionado: Int,
    onSeleccionar: (Int) -> Unit
) {
    NavigationBar(
        containerColor = BosqueProfundo,
        contentColor = Color.White
    ) {
        opciones.forEachIndexed { indice, opcion ->

            val seleccionado = indiceSeleccionado == indice

            NavigationBarItem(
                selected = seleccionado,

                onClick = {
                    onSeleccionar(indice)
                },

                icon = {
                    Icon(
                        imageVector = obtenerIcono(opcion.etiqueta),
                        contentDescription = opcion.etiqueta
                    )
                },

                label = {
                    Text(
                        text = opcion.etiqueta,
                        fontWeight = if (seleccionado) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },

                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VerdeVolt,
                    selectedTextColor = VerdeVolt,
                    unselectedIconColor = TextoInactivo,
                    unselectedTextColor = TextoInactivo,
                    indicatorColor = VerdePrincipal
                )
            )
        }
    }
}
