
package com.example.smartvolt.servicios

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Contrato para validar la presencia de una baliza.
 * La implementación actual utiliza simulación.
 */
interface BeaconValidator {
    suspend fun validar(faenaId: Long): Boolean
}

/**
 * Simulación BLE para pruebas y demostraciones.
 * No acredita proximidad física real.
 */
class SimulatedBeaconValidator(
    var detectado: Boolean = false
) : BeaconValidator {

    override suspend fun validar(faenaId: Long): Boolean {
        return detectado
    }
}

data class ResultadoGps(
    val valido: Boolean,
    val distanciaMetros: Float? = null,
    val precisionMetros: Float? = null,
    val mensaje: String
)

class UbicacionValidator(context: Context) {

    private val appContext = context.applicationContext

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    suspend fun validar(
        latitud: Double?,
        longitud: Double?,
        radioMetros: Float = 100f
    ): ResultadoGps {

        // 1. Comprobar coordenadas de la faena.
        if (
            latitud == null ||
            longitud == null ||
            latitud !in -90.0..90.0 ||
            longitud !in -180.0..180.0 ||
            radioMetros <= 0f
        ) {
            return ResultadoGps(
                valido = false,
                mensaje = "La faena no tiene coordenadas válidas"
            )
        }

        // 2. Comprobar permiso de ubicación precisa.
        val permisoPreciso = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permisoPreciso) {
            return ResultadoGps(
                valido = false,
                mensaje = "Se necesita permiso de ubicación precisa"
            )
        }

        // 3. Comprobar que la ubicación del dispositivo esté activada.
        val locationManager = appContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val ubicacionActiva = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                locationManager.isLocationEnabled
            } else {
                @Suppress("DEPRECATION")
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: Exception) {
            false
        }

        if (!ubicacionActiva) {
            return ResultadoGps(
                valido = false,
                mensaje = "Activa la ubicación del dispositivo"
            )
        }

        // 4. Obtener una ubicación actual de alta precisión.
        val ubicacion = withTimeoutOrNull(15_000L) {
            obtenerUbicacionActual()
        } ?: return ResultadoGps(
            valido = false,
            mensaje = "No fue posible obtener una ubicación reciente"
        )

        // 5. Rechazar ubicaciones simuladas.
        val ubicacionSimulada = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) {
            ubicacion.isMock
        } else {
            @Suppress("DEPRECATION")
            ubicacion.isFromMockProvider
        }

        if (ubicacionSimulada) {
            return ResultadoGps(
                valido = false,
                precisionMetros = ubicacion.accuracy.takeIf {
                    ubicacion.hasAccuracy()
                },
                mensaje = "Ubicación simulada detectada. Solicita una excepción al supervisor"
            )
        }

        // 6. Comprobar antigüedad de la ubicación.
        val edadMs = (
                SystemClock.elapsedRealtimeNanos() -
                        ubicacion.elapsedRealtimeNanos
                ) / 1_000_000

        if (edadMs < 0 || edadMs > 15_000) {
            return ResultadoGps(
                valido = false,
                mensaje = "La ubicación está desactualizada. Intenta nuevamente"
            )
        }

        // 7. Comprobar precisión.
        if (!ubicacion.hasAccuracy() || ubicacion.accuracy > 30f) {
            return ResultadoGps(
                valido = false,
                precisionMetros = ubicacion.accuracy.takeIf {
                    ubicacion.hasAccuracy()
                },
                mensaje = "La precisión GPS es insuficiente. Busca una mejor señal"
            )
        }

        // 8. Calcular distancia hasta la faena.
        val destino = Location("faena").apply {
            latitude = latitud
            longitude = longitud
        }

        val distancia = ubicacion.distanceTo(destino)

        // Se considera también el margen de incertidumbre GPS.
        val valido = distancia + ubicacion.accuracy <= radioMetros

        return ResultadoGps(
            valido = valido,
            distanciaMetros = distancia,
            precisionMetros = ubicacion.accuracy,
            mensaje = if (valido) {
                "Ubicación validada"
            } else {
                "Fuera del perímetro autorizado"
            }
        )
    }

    /**
     * Solicita una ubicación nueva utilizando Google Play Services.
     * La operación se cancela si se cancela la coroutine.
     */
    private suspend fun obtenerUbicacionActual(): Location? =
        suspendCancellableCoroutine { continuation ->

            val cancellationToken = CancellationTokenSource()

            continuation.invokeOnCancellation {
                cancellationToken.cancel()
            }

            try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                )
                    .addOnSuccessListener { ubicacion ->
                        if (continuation.isActive) {
                            continuation.resume(ubicacion)
                        }
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }

            } catch (e: SecurityException) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
}

data class ResultadoValidacion(
    val bluetoothValido: Boolean,
    val gps: ResultadoGps,
    val bluetoothSimulado: Boolean = true
) {
    val permitido: Boolean
        get() = bluetoothValido && gps.valido
}

/**
 * Coordina las validaciones necesarias para registrar asistencia.
 */
class ValidadorAsistencia(
    private val beacon: BeaconValidator,
    private val ubicacion: UbicacionValidator
) {

    suspend fun validar(
        faenaId: Long,
        lat: Double?,
        lon: Double?,
        radio: Float = 100f
    ): ResultadoValidacion {

        val ble = beacon.validar(faenaId)
        val gps = ubicacion.validar(lat, lon, radio)

        return ResultadoValidacion(
            bluetoothValido = ble,
            gps = gps,
            bluetoothSimulado = beacon is SimulatedBeaconValidator
        )
    }
}
