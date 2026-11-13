package com.example.smartvolt.ui.view.operario

import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.TextoPrincipal
import com.example.smartvolt.ui.theme.EstadoInformativo
import com.example.smartvolt.ui.theme.Blanco
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.smartvolt.util.FechaHoraUtils
import com.example.smartvolt.viewmodel.AsistenciaViewModel

@Composable
fun HistorialOperarioView(
    viewModel: AsistenciaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

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
                text = "Historial de asistencia",
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipal
            )

            Text(
                text = "Consulta tus registros de jornadas anteriores.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        // Resumen.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = VerdePrincipal
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "REGISTRO PERSONAL",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFB8C8BE)
                )

                Text(
                    text = "${uiState.historial.size} marcaciones",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Text(
                    text = "Aquí puedes consultar tus movimientos de asistencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFDCE5DD)
                )
            }
        }

        // Lista de registros.
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Registros recientes",
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )

            if (uiState.historial.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Blanco
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sin registros todavía",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextoPrincipal
                        )

                        Text(
                            text = "Cuando registres tu entrada o salida, tus marcaciones aparecerán aquí.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }

            } else {

                uiState.historial.forEach { asistencia ->
                    TarjetaHistorial(asistencia = asistencia)
                }
            }
        }
    }
}

@Composable
private fun TarjetaHistorial(
    asistencia: Asistencia
) {
    val esEntrada = asistencia.tipo == "ENTRADA"

    val colorTipo = if (esEntrada) {
        VerdePrincipal
    } else {
        EstadoInformativo
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
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = if (esEntrada) "Entrada" else "Salida",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoPrincipal
                    )

                    Text(
                        text = FechaHoraUtils.formatearFechaHora(
                            asistencia.fechaHora
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = colorTipo.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = if (esEntrada) "ENTRADA" else "SALIDA",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorTipo
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF4F6F2),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Registro #${asistencia.id}",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 9.dp
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario
                )
            }
        }
    }
}
