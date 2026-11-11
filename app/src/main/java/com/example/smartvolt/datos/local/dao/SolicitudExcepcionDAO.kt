package com.example.smartvolt.datos.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.smartvolt.modelo.Asistencia
import com.example.smartvolt.modelo.SolicitudExcepcion
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudExcepcionDAO {
    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudExcepcion): Long

    @Query("SELECT COUNT(*) FROM solicitudes_excepcion WHERE usuarioId = :usuarioId AND estado = 'PENDIENTE'")
    suspend fun contarPendientes(usuarioId: String): Int

    @Transaction
    suspend fun solicitar(solicitud: SolicitudExcepcion): Long {
        require(contarPendientes(solicitud.usuarioId) == 0) { "Ya existe una solicitud pendiente" }
        return insertarSolicitud(solicitud)
    }

    @Query("SELECT * FROM solicitudes_excepcion WHERE supervisorId = :supervisorId AND estado = 'PENDIENTE' ORDER BY fechaSolicitud DESC")
    fun pendientes(supervisorId: String): Flow<List<SolicitudExcepcion>>

    @Query("SELECT * FROM solicitudes_excepcion WHERE usuarioId = :usuarioId ORDER BY fechaSolicitud DESC")
    fun porOperario(usuarioId: String): Flow<List<SolicitudExcepcion>>

    @Query("SELECT * FROM solicitudes_excepcion WHERE id = :id")
    suspend fun obtener(id: Long): SolicitudExcepcion?

    @Query("SELECT * FROM asistencias WHERE usuarioId = :usuarioId ORDER BY fechaHora DESC, id DESC LIMIT 1")
    suspend fun ultimaMarcacion(usuarioId: String): Asistencia?

    @Query("UPDATE solicitudes_excepcion SET estado = :estado, fechaResolucion = :fecha, observacion = :nota WHERE id = :id AND estado = 'PENDIENTE'")
    suspend fun resolverEstado(id: Long, estado: String, fecha: Long, nota: String): Int

    @Insert
    suspend fun agregarAsistencia(asistencia: Asistencia): Long

    @Transaction
    suspend fun resolver(id: Long, supervisorId: String, aprobar: Boolean, nota: String): Boolean {
        val solicitud = obtener(id) ?: return false
        if (solicitud.estado != "PENDIENTE" || solicitud.supervisorId != supervisorId) return false
        if (aprobar) {
            val ultima = ultimaMarcacion(solicitud.usuarioId)
            val siguiente = if (ultima?.tipo == "ENTRADA") "SALIDA" else "ENTRADA"
            if (siguiente != solicitud.tipo) return false
        }
        if (resolverEstado(id, if (aprobar) "APROBADA" else "RECHAZADA", System.currentTimeMillis(), nota) != 1) return false
        if (aprobar) {
            agregarAsistencia(Asistencia(usuarioId = solicitud.usuarioId, fechaHora = solicitud.fechaSolicitud,
                tipo = solicitud.tipo, faenaId = solicitud.faenaId, metodoRegistro = "EXCEPCIONAL", solicitudId = id,
                validacionBluetooth = if (solicitud.bluetoothValido) "VALIDO" else "INVALIDO",
                validacionGps = if (solicitud.gpsValido) "VALIDO" else "INVALIDO"))
        }
        return true
    }
}
