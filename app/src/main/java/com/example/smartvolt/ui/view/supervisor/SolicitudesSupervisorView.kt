package com.example.smartvolt.ui.view.supervisor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.SolicitudExcepcionRepositorio
import kotlinx.coroutines.launch

@Composable
fun SolicitudesSupervisorView(supervisorId: String) {
    val context = LocalContext.current
    val repo = remember(context) { SolicitudExcepcionRepositorio(AppDataBase.obtenerBaseDatos(context).solicitudExcepcionDAO()) }
    val solicitudes by repo.pendientes(supervisorId).collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Solicitudes excepcionales", style = MaterialTheme.typography.titleLarge)
        Text("Demostración local: las solicitudes no se envían a otro teléfono.", style = MaterialTheme.typography.bodySmall)
        mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        if (solicitudes.isEmpty()) Text("No hay solicitudes pendientes.")
        solicitudes.forEach { solicitud ->
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Operario: ${solicitud.usuarioId} · ${solicitud.tipo}")
                    Text("Motivo: ${solicitud.motivo}")
                    Text("BLE: ${if (solicitud.bluetoothValido) "válido" else "no detectado"} / GPS: ${if (solicitud.gpsValido) "válido" else "no válido"}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { scope.launch { mensaje = if (repo.resolver(solicitud.id, supervisorId, true, "Aprobado en demo")) "Solicitud aprobada" else "No se puede aprobar: revisa la secuencia de marcaciones" } }) { Text("Aprobar") }
                        OutlinedButton(onClick = { scope.launch { mensaje = if (repo.resolver(solicitud.id, supervisorId, false, "Rechazado en demo")) "Solicitud rechazada" else "No se pudo rechazar" } }) { Text("Rechazar") }
                    }
                }
            }
        }
    }
}
