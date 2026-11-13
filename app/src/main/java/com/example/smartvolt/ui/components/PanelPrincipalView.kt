
package com.example.smartvolt.ui.components

import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.TextoHeaderClaro
import com.example.smartvolt.ui.theme.SuperficieSuave
import com.example.smartvolt.ui.theme.Fondo
import com.example.smartvolt.ui.theme.BosqueProfundo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Identidad visual SmartVolt
@Composable
fun PanelPrincipalView(
    nombreRol: String,
    titulo: String,
    subtitulo: String,
    modulos: List<String>,
    onCerrarSesion: () -> Unit,
    contenidoPersonalizado: (@Composable (Int, (Int) -> Unit) -> Unit)? = null
) {
    var indiceSeleccionado by rememberSaveable(nombreRol) {
        mutableStateOf(0)
    }

    val opciones = when (nombreRol.trim().uppercase()) {
        "OPERARIO" -> listOf(
            OpcionNavegacion("Inicio", "⌂"),
            OpcionNavegacion("Asistencia", "◷"),
            OpcionNavegacion("Historial", "▤"),
            OpcionNavegacion("Perfil", "●")
        )

        "SUPERVISOR" -> listOf(
            OpcionNavegacion("Inicio", "⌂"),
            OpcionNavegacion("Equipo", "♧"),
            OpcionNavegacion("Asistencia", "◷"),
            OpcionNavegacion("Reportes", "▤")
        )

        "ADMINISTRADOR" -> listOf(
            OpcionNavegacion("Inicio", "⌂"),
            OpcionNavegacion("Usuarios", "♧"),
            OpcionNavegacion("Faenas", "▦"),
            OpcionNavegacion("Ajustes", "⚙")
        )

        else -> emptyList()
    }

    Scaffold(
        containerColor = Fondo,
        bottomBar = {
            NavegacionInferior(
                opciones = opciones,
                indiceSeleccionado = indiceSeleccionado,
                onSeleccionar = { nuevoIndice ->
                    indiceSeleccionado = nuevoIndice
                }
            )
        }
    ) { espacioContenido ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacioContenido)
                .background(Fondo)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Encabezado superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "SMARTVOLT",
                        style = MaterialTheme.typography.titleLarge,
                        color = BosqueProfundo,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "GESTIÓN DE FAENAS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextoSecundario,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = BosqueProfundo,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = nombreRol,
                        modifier = Modifier.padding(
                            horizontal = 15.dp,
                            vertical = 9.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = VerdeVolt,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Contenido de la sección seleccionada
            if (contenidoPersonalizado != null) {

                contenidoPersonalizado(indiceSeleccionado) { nuevoIndice ->
                    indiceSeleccionado = nuevoIndice
                }

            } else {

                // Encabezado principal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    BosqueProfundo,
                                    VerdePrincipal
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SMARTVOLT",
                            style = MaterialTheme.typography.labelLarge,
                            color = VerdeVolt,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = subtitulo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoHeaderClaro
                        )

                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = "PANEL $nombreRol",
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = VerdeVolt,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Encabezado de módulos
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Módulos disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = BosqueProfundo,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Accesos rápidos correspondientes a tu perfil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }

                // Tarjetas de módulos
                modulos.forEach { modulo ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = modulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BosqueProfundo,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Módulo en preparación",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            }

                            Surface(
                                color = SuperficieSuave,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = "PRONTO",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerdePrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Cerrar sesión
            Button(
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC64B4B)
                )
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
