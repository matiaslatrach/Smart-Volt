package com.example.smartvolt.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class OpcionNavegacion(
    val etiqueta: String,
    val simbolo: String
)

private val AzulOscuro = Color(0xFF101D32)
private val VerdeVolt = Color(0xFFD6F36A)
private val TextoInactivo = Color(0xFF94A3B8)

@Composable
fun NavegacionInferior(
    opciones: List<OpcionNavegacion>,
    indiceSeleccionado: Int,
    onSeleccionar: (Int) -> Unit
) {
    NavigationBar(
        containerColor = AzulOscuro
    ) {
        opciones.forEachIndexed { indice, opcion ->
            val seleccionado = indiceSeleccionado == indice

            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    onSeleccionar(indice)
                },
                icon = {
                    Text(
                        text = opcion.simbolo,
                        color = if (seleccionado) VerdeVolt else TextoInactivo
                    )
                },
                label = {
                    Text(
                        text = opcion.etiqueta,
                        color = if (seleccionado) VerdeVolt else TextoInactivo
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF1D304A)
                )
            )
        }
    }
}
