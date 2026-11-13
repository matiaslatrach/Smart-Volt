package com.example.smartvolt.servicios

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.example.smartvolt.modelo.Faena
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/** Modo demostración: Bluetooth es real, pero NO hay ninguna baliza física. */
class BluetoothDemo(private val context: Context) {
    fun estaEncendido(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED
        ) return false

        return try {
            val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            manager?.adapter?.isEnabled == true
        } catch (_: SecurityException) {
            false
        }
    }
}

data class ResultadoGps(
    val valido: Boolean,
    val mensaje: String,
    val distanciaMetros: Float? = null
)

/** Una marcación de entrada requiere coordenadas de faena, GPS real y señal suficiente. */
class ValidacionGps(private val context: Context) {
    private val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun estaEncendido(): Boolean = try {
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (_: Exception) {
        false
    }

    suspend fun comprobar(faena: Faena?): ResultadoGps {
        if (faena == null || !faena.activa) {
            return ResultadoGps(false, "Necesitas una faena activa asignada.")
        }
        val latitud = faena.latitud
        val longitud = faena.longitud
        if (latitud == null || longitud == null ||
            latitud !in -90.0..90.0 || longitud !in -180.0..180.0
        ) return ResultadoGps(false, "El administrador debe configurar las coordenadas de la faena.")

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) return ResultadoGps(false, "Autoriza la ubicación precisa para continuar.")

        if (!estaEncendido()) return ResultadoGps(false, "Activa el GPS del teléfono.")

        // Solicitar una lectura nueva; no confiar solo en la última ubicación almacenada.
        val ubicacion = obtenerLecturaActual()
            ?: return ResultadoGps(false, "No se obtuvo señal GPS reciente. Prueba al aire libre.")

        val esSimulada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ubicacion.isMock
        } else {
            @Suppress("DEPRECATION")
            ubicacion.isFromMockProvider
        }
        if (esSimulada) {
            return ResultadoGps(false, "Ubicación simulada detectada; no se permite marcar.")
        }

        val antiguedadMs = (SystemClock.elapsedRealtimeNanos() - ubicacion.elapsedRealtimeNanos) / 1_000_000
        if (antiguedadMs !in 0L..15_000L) {
            return ResultadoGps(false, "La posición GPS no está actualizada.")
        }
        if (!ubicacion.hasAccuracy() || ubicacion.accuracy > 30f) {
            return ResultadoGps(false, "Se necesita mejor precisión GPS (30 m o menos).")
        }

        val distancia = FloatArray(1)
        Location.distanceBetween(
            ubicacion.latitude, ubicacion.longitude, latitud, longitud, distancia
        )
        val metros = distancia[0]
        // Incluimos el error estimado para evitar dar por válida una posición dudosa.
        val dentro = ReglasEntrada.dentroDelRadio(metros, ubicacion.accuracy)
        return if (dentro) {
            ResultadoGps(true, "Ubicación válida: ${metros.toInt()} m de la faena.", metros)
        } else {
            ResultadoGps(false, "Fuera del radio seguro de 100 m (${metros.toInt()} m).", metros)
        }
    }

    private suspend fun obtenerLecturaActual(): Location? = withContext(Dispatchers.Main.immediate) {
        withTimeoutOrNull(15_000L) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        manager.removeUpdates(this)
                        if (continuation.isActive) continuation.resume(location)
                    }
                }
                continuation.invokeOnCancellation { manager.removeUpdates(listener) }
                try {
                    manager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0L, 0f, listener, Looper.getMainLooper()
                    )
                } catch (_: SecurityException) {
                    if (continuation.isActive) continuation.resume(null)
                } catch (_: IllegalArgumentException) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        }
    }
}

