package com.example.smartvolt.ui.view.operario

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.SolicitudExcepcionRepositorio
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.modelo.SolicitudExcepcion
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.servicios.*
import com.example.smartvolt.ui.theme.smartVoltTextFieldColors
import com.example.smartvolt.viewmodel.AsistenciaViewModel
import kotlinx.coroutines.launch

@Composable
fun AsistenciaOperarioView(viewModel: AsistenciaViewModel, usuario: Usuario, faena: Faena?) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val beacon = remember { SimulatedBeaconValidator(false) }
    val gps = remember(context) { UbicacionValidator(context) }
    val solicitudes = remember(context) { SolicitudExcepcionRepositorio(AppDataBase.obtenerBaseDatos(context).solicitudExcepcionDAO()) }
    var beaconPresente by remember { mutableStateOf(false) }
    var validando by remember { mutableStateOf(false) }
    var resultado by remember { mutableStateOf<ResultadoValidacion?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var motivo by remember { mutableStateOf("") }
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] != true) {
            resultado = ResultadoValidacion(beaconPresente, ResultadoGps(false, mensaje = "Permiso de ubicación precisa denegado"))
            mensaje = "Puedes solicitar una excepción"
        } else resultado = null
    }
    val siguiente = if (estado.ultimaAsistencia?.tipo == "ENTRADA") "SALIDA" else "ENTRADA"

    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Control de asistencia", style = MaterialTheme.typography.headlineSmall)
        Text("Bluetooth simulado (DEMO) y ubicación GPS real · radio 100 m")
        if (faena == null) Text("No tienes faena asignada", color = MaterialTheme.colorScheme.error)
        else if (faena.latitud == null || faena.longitud == null)
            Text("El administrador debe configurar la latitud y longitud de tu faena", color = MaterialTheme.colorScheme.error)
        else Text("Faena: ${faena.nombre}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Simular baliza detectada")
            Switch(checked = beaconPresente, onCheckedChange = { beaconPresente = it; resultado = null })
        }
        Button(enabled = faena != null && !validando, onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                pedirPermiso.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            } else {
                val actual = faena ?: return@Button
                validando = true
                scope.launch {
                    try {
                        beacon.detectado = beaconPresente
                        resultado = ValidadorAsistencia(beacon, gps).validar(actual.id, actual.latitud, actual.longitud)
                        mensaje = null
                    } catch (e: Exception) { mensaje = "No fue posible validar el dispositivo" }
                    finally { validando = false }
                }
            }
        }) { Text(if (validando) "Validando..." else "Comprobar requisitos") }
        resultado?.let {
            Text("Bluetooth demo: ${if (it.bluetoothValido) "detectado" else "no detectado"}")
            Text("GPS: ${it.gps.mensaje}")
            it.gps.distanciaMetros?.let { distancia -> Text("Distancia aproximada: ${distancia.toInt()} m") }
        }
        Button(enabled = resultado?.permitido == true && faena != null && !validando && !estado.cargando, onClick = {
            val actual = faena ?: return@Button
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                pedirPermiso.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            } else {
                validando = true
                scope.launch {
                    try {
                        beacon.detectado = beaconPresente
                        val nuevo = ValidadorAsistencia(beacon, gps).validar(actual.id, actual.latitud, actual.longitud)
                        resultado = nuevo
                        if (nuevo.permitido) viewModel.registrarMarcacion(nuevo, actual.id)
                        else mensaje = "Validación fallida: solicita una excepción"
                    } catch (e: Exception) { mensaje = "Error de validación" }
                    finally { validando = false }
                }
            }
        }) { Text(if (siguiente == "ENTRADA") "Registrar entrada" else "Registrar salida") }
        estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        estado.mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        mensaje?.let { Text(it) }
        HorizontalDivider()
        Text("Solicitud excepcional (requiere supervisor, misma instalación durante la demo)")
        OutlinedTextField(value = motivo, onValueChange = { motivo = it }, label = { Text("Motivo") }, modifier = Modifier.fillMaxWidth(), colors = smartVoltTextFieldColors())
        OutlinedButton(enabled = faena != null && usuario.supervisorId != null && motivo.isNotBlank() && !validando && resultado != null && resultado?.permitido == false, onClick = {
            val actual = faena ?: return@OutlinedButton
            val supervisor = usuario.supervisorId ?: return@OutlinedButton
            scope.launch {
                try {
                    val r = resultado
                    solicitudes.solicitar(SolicitudExcepcion(usuarioId = usuario.id, supervisorId = supervisor,
                        faenaId = actual.id, tipo = siguiente, fechaSolicitud = System.currentTimeMillis(),
                        motivo = motivo.trim(), bluetoothValido = r?.bluetoothValido == true, gpsValido = r?.gps?.valido == true))
                    mensaje = "Solicitud pendiente de aprobación (demo local)"
                    motivo = ""
                } catch (e: Exception) { mensaje = "No fue posible crear la solicitud" }
            }
        }) { Text("Solicitar excepción") }
    }
}
