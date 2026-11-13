package com.example.smartvolt.ui.view.operario

import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.Blanco
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartvolt.modelo.Asistencia
import com.example.smartvolt.viewmodel.AsistenciaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AsistenciaOperarioView(
    viewModel: AsistenciaViewModel
) {

    val estado by viewModel.uiState.collectAsState()

    val ultimaAsistencia = estado.ultimaAsistencia

    val jornadaIniciada = ultimaAsistencia?.tipo == "ENTRADA"

    val estadoTexto = when {
        ultimaAsistencia == null -> "SIN REGISTRO"
        jornadaIniciada -> "JORNADA EN CURSO"
        else -> "JORNADA FINALIZADA"
    }

    val textoBoton = if (jornadaIniciada) {
        "Registrar salida"
    } else {
        "Registrar entrada"
    }

    val colorEstado = when {
        ultimaAsistencia == null -> Color(0xFF718078)
        jornadaIniciada -> Color(0xFF2E7D5B)
        else -> Color(0xFF397A8A)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Encabezado.
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Control de asistencia",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF172522)
            )

            Text(
                text = "Registra y consulta tu jornada laboral.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        // Estado actual de la jornada.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "ESTADO ACTUAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario
                )

                Surface(
                    color = colorEstado.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = estadoTexto,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 9.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorEstado
                    )
                }

                Text(
                    text = when {
                        ultimaAsistencia == null ->
                            "Todavía no has registrado tu entrada."

                        jornadaIniciada ->
                            "Tu jornada laboral se encuentra iniciada. Recuerda registrar tu salida al finalizar."

                        else ->
                            "Tu última jornada fue finalizada correctamente."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF172522)
                )

                if (ultimaAsistencia != null) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF4F6F2),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "ÚLTIMA MARCACIÓN",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextoSecundario
                            )

                            Text(
                                text = ultimaAsistencia.tipo,
                                style = MaterialTheme.typography.titleMedium,
                                color = VerdePrincipal
                            )

                            Text(
                                text = formatearFecha(ultimaAsistencia.fechaHora),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                    }
                }

                // Acción principal.
                Button(
                    onClick = {
                        viewModel.registrarMarcacion()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = !estado.cargando,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Color.White,
                        disabledContainerColor = VerdePrincipal.copy(alpha = 0.5f)
                    )
                ) {
                    if (estado.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = textoBoton,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                estado.mensaje?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = Color(0xFF2E7D5B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                estado.mensajeError?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFC64B4B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Historial.
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Mis marcaciones",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF172522)
                )

                Text(
                    text = "Consulta tus últimos registros de asistencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            if (estado.historial.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Blanco
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sin registros todavía",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF172522)
                        )

                        Text(
                            text = "Cuando registres tu entrada o salida, aparecerá aquí.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }

            } else {

                estado.historial.forEach { asistencia ->
                    TarjetaMarcacion(asistencia)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TarjetaMarcacion(
    asistencia: Asistencia
) {

    val esEntrada = asistencia.tipo == "ENTRADA"

    val colorTipo = if (esEntrada) {
        VerdePrincipal
    } else {
        Color(0xFF397A8A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = colorTipo.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = asistencia.tipo,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorTipo
                    )
                }

                Text(
                    text = formatearFecha(asistencia.fechaHora),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            Text(
                text = "#${asistencia.id}",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario
            )
        }
    }
}

private fun formatearFecha(fechaHora: Long): String {

    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale.getDefault()
    )

    return formato.format(Date(fechaHora))
}
