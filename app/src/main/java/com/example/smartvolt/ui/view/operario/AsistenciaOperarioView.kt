package com.example.smartvolt.ui.view.operario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Control de asistencia",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Registra y consulta tu jornada laboral.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Tarjeta del estado actual.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Estado de asistencia",
                    style = MaterialTheme.typography.titleMedium
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = estadoTexto,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when {
                        ultimaAsistencia == null ->
                            "Todavía no has registrado tu entrada."

                        jornadaIniciada ->
                            "Tu jornada laboral se encuentra iniciada."

                        else ->
                            "Tu última jornada fue finalizada."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (ultimaAsistencia != null) {

                    Text(
                        text = "Última marcación: ${ultimaAsistencia.tipo}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = formatearFecha(ultimaAsistencia.fechaHora),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        viewModel.registrarMarcacion()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !estado.cargando
                ) {

                    if (estado.cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = textoBoton)
                    }
                }

                estado.mensaje?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                estado.mensajeError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Historial de marcaciones.
        Text(
            text = "Mis últimas marcaciones",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (estado.historial.isEmpty()) {

            Text(
                text = "Todavía no tienes marcaciones registradas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            estado.historial.forEach { asistencia ->

                TarjetaMarcacion(asistencia)
            }
        }
    }
}

@Composable
private fun TarjetaMarcacion(
    asistencia: Asistencia
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = asistencia.tipo,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = formatearFecha(asistencia.fechaHora),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "#${asistencia.id}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
