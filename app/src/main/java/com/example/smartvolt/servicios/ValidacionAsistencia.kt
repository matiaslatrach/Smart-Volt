package com.example.smartvolt.servicios

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/** La demo BLE es explícita: no demuestra proximidad física real. */
interface BeaconValidator {
    suspend fun validar(faenaId: Long): Boolean
}

class SimulatedBeaconValidator(var detectado: Boolean = false) : BeaconValidator {
    override suspend fun validar(faenaId: Long) = detectado
}

data class ResultadoGps(
    val valido: Boolean,
    val distanciaMetros: Float? = null,
    val precisionMetros: Float? = null,
    val mensaje: String
)

class UbicacionValidator(private val context: Context) {
    suspend fun validar(latitud: Double?, longitud: Double?, radioMetros: Float = 100f): ResultadoGps {
        if (latitud == null || longitud == null || latitud !in -90.0..90.0 || longitud !in -180.0..180.0) {
            return ResultadoGps(false, mensaje = "La faena no tiene coordenadas configuradas")
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ResultadoGps(false, mensaje = "Se necesita permiso de ubicación precisa")
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return ResultadoGps(false, mensaje = "Activa la ubicación del dispositivo")
        }
        val ubicacion = withTimeoutOrNull(15_000) { obtenerActual(manager, provider) }
            ?: return ResultadoGps(false, mensaje = "No fue posible obtener una ubicación reciente")
        // Rechazar ubicaciones marcadas como simuladas por Android.
        // No es una garantía absoluta frente a dispositivos comprometidos.
        val simulada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ubicacion.isMock
        } else {
            @Suppress("DEPRECATION")
            ubicacion.isFromMockProvider
        }
        if (simulada) {
            return ResultadoGps(false, mensaje = "Ubicación simulada detectada. Solicita una excepción al supervisor")
        }
        val edadMs = (android.os.SystemClock.elapsedRealtimeNanos() - ubicacion.elapsedRealtimeNanos) / 1_000_000
        if (edadMs < 0 || edadMs > 15_000 || !ubicacion.hasAccuracy() || ubicacion.accuracy > 30f) {
            return ResultadoGps(false, precisionMetros = if (ubicacion.hasAccuracy()) ubicacion.accuracy else null,
                mensaje = "La precisión GPS es insuficiente")
        }
        val destino = Location("faena").apply { latitude = latitud; longitude = longitud }
        val distancia = ubicacion.distanceTo(destino)
        val valido = distancia + ubicacion.accuracy <= radioMetros
        return ResultadoGps(valido, distancia, ubicacion.accuracy,
            if (valido) "Ubicación validada" else "Fuera del perímetro autorizado")
    }

    private suspend fun obtenerActual(manager: LocationManager, provider: String): Location? =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : android.location.LocationListener {
                    override fun onLocationChanged(location: Location) {
                        manager.removeUpdates(this)
                        if (continuation.isActive) continuation.resume(location)
                    }
                    override fun onProviderDisabled(provider: String) {
                        manager.removeUpdates(this)
                        if (continuation.isActive) continuation.resume(null)
                    }
                }
                try {
                    @Suppress("DEPRECATION")
                    manager.requestSingleUpdate(provider, listener, android.os.Looper.getMainLooper())
                    continuation.invokeOnCancellation { manager.removeUpdates(listener) }
                } catch (e: SecurityException) {
                    if (continuation.isActive) continuation.resume(null)
                } catch (e: IllegalArgumentException) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        }
}

data class ResultadoValidacion(
    val bluetoothValido: Boolean,
    val gps: ResultadoGps,
    val demostracion: Boolean = true
) {
    val permitido get() = bluetoothValido && gps.valido
}

class ValidadorAsistencia(
    private val beacon: BeaconValidator,
    private val ubicacion: UbicacionValidator
) {
    suspend fun validar(faenaId: Long, lat: Double?, lon: Double?, radio: Float = 100f): ResultadoValidacion {
        val ble = beacon.validar(faenaId)
        val gps = ubicacion.validar(lat, lon, radio)
        return ResultadoValidacion(ble, gps)
    }
}
