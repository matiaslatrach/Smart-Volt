
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

    @Transaction
    suspend fun registrarSiguienteMarcacion(
        usuarioId: String,
        fechaHora: Long,
        entradaValidada: Boolean = false,
        tipoEsperado: String? = null
    ): Asistencia {

        val ultima = obtenerUltimaAsistencia(usuarioId)

        val tipo = when (ultima?.tipo) {
            "ENTRADA" -> "SALIDA"
            "SALIDA", null -> "ENTRADA"
            else -> throw IllegalStateException(
                "Tipo de asistencia no válido."
            )
        }

        if (tipoEsperado != null && tipo != tipoEsperado) {
            throw IllegalStateException("La jornada cambió de estado. Vuelve a revisar la marcación.")
        }

        // La validación también se exige en la capa de datos, no solo en la UI.
        if (tipo == "ENTRADA" && !entradaValidada) {
            throw IllegalStateException("Verifica Bluetooth y GPS antes de registrar la entrada.")
        }

        val asistencia = Asistencia(
            usuarioId = usuarioId,
            fechaHora = fechaHora,
            tipo = tipo,
            metodoRegistro = if (tipo == "ENTRADA") "DEMO_BLE_GPS" else "SALIDA"
        )

        registrarAsistencia(asistencia)

        return asistencia
    }
}
