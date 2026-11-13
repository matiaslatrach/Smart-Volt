package com.example.smartvolt.ui.view.operario

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.servicios.BluetoothDemo
import com.example.smartvolt.servicios.ReglasEntrada
import com.example.smartvolt.servicios.ResultadoGps
import com.example.smartvolt.servicios.ValidacionGps
import com.example.smartvolt.ui.theme.EstadoError
import com.example.smartvolt.ui.theme.EstadoExitoso
import com.example.smartvolt.ui.theme.TextoPrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdePrincipal
import kotlinx.coroutines.launch

/** Solo la ENTRADA necesita Bluetooth encendido, baliza DEMO y GPS real. */
@Composable
fun ValidacionEntradaView(
    faena: Faena?,
    ocupado: Boolean,
    onValidada: () -> Unit
) {
    val context = LocalContext.current
    val cicloVida = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val bluetooth = remember(context) { BluetoothDemo(context) }
    val gps = remember(context) { ValidacionGps(context) }

    var bluetoothEncendido by remember { mutableStateOf(bluetooth.estaEncendido()) }
    var balizaDemo by remember { mutableStateOf(false) }
    var resultadoGps by remember(faena?.id, faena?.latitud, faena?.longitud) {
        mutableStateOf<ResultadoGps?>(null)
    }
    var verificando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf<String?>(null) }

    // Refrescar el Bluetooth si el operario cambia el estado desde ajustes rápidos.
    DisposableEffect(cicloVida) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) {
                bluetoothEncendido = bluetooth.estaEncendido()
                if (!bluetoothEncendido) balizaDemo = false
            }
        }
        cicloVida.lifecycle.addObserver(observador)
        onDispose { cicloVida.lifecycle.removeObserver(observador) }
    }

    val activarBluetooth = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        bluetoothEncendido = bluetooth.estaEncendido()
        if (!bluetoothEncendido) aviso = "Debes encender Bluetooth para continuar."
    }

    val abrirActivacionBluetooth: () -> Unit = {
        try {
            activarBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } catch (_: RuntimeException) {
            aviso = "No fue posible abrir la activación Bluetooth en este dispositivo."
        }
    }

    val permisoBluetooth = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirActivacionBluetooth()
        } else {
            aviso = "Autoriza dispositivos cercanos para comprobar Bluetooth."
        }
    }

    val ajustesGps = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        resultadoGps = null // La ubicación debe comprobarse nuevamente al volver.
    }

    val comprobarGps: () -> Unit = {
        scope.launch {
            verificando = true
            aviso = null
            try {
                resultadoGps = gps.comprobar(faena)
            } finally {
                verificando = false
            }
        }
    }

    val permisoGps = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] != true) {
            aviso = "Concede ubicación precisa, no solo aproximada."
        } else if (!gps.estaEncendido()) {
            aviso = "Activa la ubicación del teléfono y vuelve a comprobar GPS."
            ajustesGps.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            comprobarGps()
        }
    }

    val faenaLista = faena?.let { it.activa && it.latitud != null && it.longitud != null } == true
    val puedeEntrar = ReglasEntrada.habilitada(
        bluetoothEncendido, balizaDemo, resultadoGps?.valido == true, faenaLista
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Validaciones de entrada",
            color = TextoPrincipal,
            style = MaterialTheme.typography.titleMedium
        )

        // El teléfono debe tener Bluetooth realmente encendido.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bluetooth", color = TextoPrincipal)
            EstadoVerificacion(if (bluetoothEncendido) "ACTIVO" else "PENDIENTE", bluetoothEncendido)
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !ocupado && !verificando,
            onClick = {
                aviso = null
                if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
                    aviso = "Este teléfono no cuenta con Bluetooth. Usa un equipo Android físico."
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    permisoBluetooth.launch(Manifest.permission.BLUETOOTH_CONNECT)
                } else if (bluetooth.estaEncendido()) {
                    bluetoothEncendido = true
                    aviso = "Bluetooth está encendido. Activa ahora la baliza de demostración."
                } else {
                    abrirActivacionBluetooth()
                }
            }
        ) { Text("Activar / comprobar Bluetooth") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Simular baliza detectada", color = TextoPrincipal)
                Text("MODO DEMO: no detecta una baliza física.",
                    color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = balizaDemo && bluetoothEncendido,
                enabled = bluetoothEncendido && !ocupado && !verificando,
                onCheckedChange = { balizaDemo = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ubicación GPS", color = TextoPrincipal)
            EstadoVerificacion(if (resultadoGps?.valido == true) "VÁLIDO" else "PENDIENTE",
                resultadoGps?.valido == true)
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !ocupado && !verificando,
            onClick = {
                aviso = null
                resultadoGps = null
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    permisoGps.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                } else if (!gps.estaEncendido()) {
                    aviso = "Activa el GPS en ajustes y vuelve a comprobar."
                    ajustesGps.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else {
                    comprobarGps()
                }
            }
        ) { Text("Activar / comprobar ubicación") }

        if (!faenaLista) {
            Text("El administrador debe asignarte una faena activa y configurar sus coordenadas.",
                color = EstadoError, style = MaterialTheme.typography.bodySmall)
        }
        resultadoGps?.let { resultado ->
            Text(resultado.mensaje,
                color = if (resultado.valido) EstadoExitoso else EstadoError,
                style = MaterialTheme.typography.bodySmall)
        }
        aviso?.let {
            Text(it, color = EstadoError, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            "Para registrar la entrada se exige GPS real dentro de 100 m, con precisión de 30 m o mejor. " +
                "Al marcar, verificaremos la ubicación otra vez.",
            color = TextoSecundario,
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = puedeEntrar && !ocupado && !verificando,
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
            onClick = {
                scope.launch {
                    verificando = true
                    try {
                        bluetoothEncendido = bluetooth.estaEncendido()
                        if (ReglasEntrada.habilitada(
                                bluetoothEncendido, balizaDemo, true, faenaLista
                            )) {
                            // Verificación nueva, nunca reutilizar una posición anterior.
                            val lectura = gps.comprobar(faena)
                            resultadoGps = lectura
                            if (lectura.valido && bluetooth.estaEncendido()) {
                                onValidada()
                            } else if (!lectura.valido) {
                                aviso = lectura.mensaje
                            } else {
                                aviso = "Bluetooth se desactivó. Intenta nuevamente."
                            }
                        } else {
                            aviso = "Activa Bluetooth y la baliza DEMO antes de continuar."
                        }
                    } finally {
                        verificando = false
                    }
                }
            }
        ) {
            if (verificando || ocupado) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = androidx.compose.ui.graphics.Color.White,
                    strokeWidth = 2.dp
                )
            }
            else Text("Registrar entrada")
        }

        Text("Atención: la baliza es simulada y la marcación se guarda como DEMO_BLE_GPS.",
            color = TextoSecundario, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun EstadoVerificacion(texto: String, correcto: Boolean) {
    Surface(color = if (correcto) EstadoExitoso.copy(alpha = 0.12f)
                   else EstadoError.copy(alpha = 0.10f)) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            color = if (correcto) EstadoExitoso else EstadoError,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
