
package com.example.smartvolt.datos.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.smartvolt.modelo.Asistencia
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrarAsistencia(asistencia: Asistencia): Long

    @Query("""
        SELECT * FROM asistencias
        WHERE usuarioId = :usuarioId
        ORDER BY fechaHora DESC, id DESC
    """)
    fun listarAsistenciasPorUsuario(
        usuarioId: String
    ): Flow<List<Asistencia>>

    @Query("""
        SELECT * FROM asistencias
        WHERE usuarioId = :usuarioId
        ORDER BY fechaHora DESC, id DESC
        LIMIT 1
    """)
    suspend fun obtenerUltimaAsistencia(
        usuarioId: String
    ): Asistencia?

    @Query("""
        SELECT asistencias.*
        FROM asistencias
        INNER JOIN usuarios
            ON asistencias.usuarioId = usuarios.id
        WHERE usuarios.supervisorId = :supervisorId
          AND usuarios.rol = 'OPERARIO'
        ORDER BY asistencias.fechaHora DESC, asistencias.id DESC
    """)
    fun listarAsistenciasPorSupervisor(
        supervisorId: String
    ): Flow<List<Asistencia>>

    @Query("SELECT faenaId FROM usuarios WHERE id = :usuarioId AND activo = 1")
    suspend fun obtenerFaenaUsuarioActivo(usuarioId: String): Long?

    @Query("SELECT COUNT(*) FROM faenas WHERE id = :faenaId AND activa = 1 AND latitud IS NOT NULL AND longitud IS NOT NULL")
    suspend fun faenaValida(faenaId: Long): Int

    @Transaction
    suspend fun registrarSiguienteMarcacion(
        usuarioId: String,
        fechaHora: Long,
        faenaId: Long,
        validacionBluetooth: String,
        validacionGps: String,
        distanciaMetros: Double?,
        precisionMetros: Float?
    ): Asistencia {
        require(validacionBluetooth == "VALIDO" && validacionGps == "VALIDO")
        require(obtenerFaenaUsuarioActivo(usuarioId) == faenaId && faenaValida(faenaId) == 1) {
            "Usuario o faena no autorizados"
        }

        val ultima = obtenerUltimaAsistencia(usuarioId)

        val tipo = when (ultima?.tipo) {
            "ENTRADA" -> "SALIDA"
            "SALIDA", null -> "ENTRADA"
            else -> throw IllegalStateException(
                "Tipo de asistencia no válido."
            )
        }

        val asistencia = Asistencia(
            usuarioId = usuarioId,
            fechaHora = fechaHora,
            tipo = tipo,
            faenaId = faenaId,
            metodoRegistro = "DEMO_BLE",
            validacionBluetooth = validacionBluetooth,
            validacionGps = validacionGps,
            distanciaMetros = distanciaMetros,
            precisionMetros = precisionMetros
        )

        registrarAsistencia(asistencia)

        return asistencia
    }
}
