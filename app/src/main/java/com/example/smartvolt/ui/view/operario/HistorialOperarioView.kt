package com.example.smartvolt.ui.view.operario

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartvolt.dominio.JornadaCalculator
import com.example.smartvolt.viewmodel.AsistenciaViewModel

@Composable
fun HistorialOperarioView(viewModel: AsistenciaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val jornadas = remember(uiState.historial) { JornadaCalculator.agrupar(uiState.historial) }
    val totalMs = jornadas.mapNotNull { it.duracionMs }.sum()
    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Historial de jornadas", style = MaterialTheme.typography.headlineSmall)
        Text("${jornadas.count { it.salida != null }} jornadas completas · ${JornadaCalculator.formatoDuracion(totalMs)} registradas")
        Text("Duraciones calculadas entre entrada y salida, sin descuento de colación.", style = MaterialTheme.typography.bodySmall)
        if (jornadas.isEmpty()) Text("Todavía no tienes jornadas registradas")
        jornadas.forEach { jornada ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(JornadaCalculator.formatoFecha(jornada.entrada.fechaHora), style = MaterialTheme.typography.titleMedium)
                    Text("Entrada: ${JornadaCalculator.formatoHora(jornada.entrada.fechaHora)}")
                    Text("Salida: ${jornada.salida?.let { JornadaCalculator.formatoHora(it.fechaHora) } ?: "Pendiente"}")
                    Text("Duración: ${JornadaCalculator.formatoDuracion(jornada.duracionMs)}")
                    if (jornada.incidencia) Text("Revisar marcaciones incompletas", color = MaterialTheme.colorScheme.error)
                    Text("Origen: ${jornada.entrada.metodoRegistro}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
