package com.example.smartvolt.datos.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartvolt.modelo.Faena
import kotlinx.coroutines.flow.Flow

@Dao
interface FaenaDAO {

    @Query("SELECT * FROM faenas ORDER BY id DESC")
    fun listarFaenas(): Flow<List<Faena>>

    @Query("SELECT * FROM faenas WHERE id = :id")
    suspend fun obtenerFaena(id: Long): Faena?

    /**
     * Agrega una faena de demostración una sola vez, incluso si la app
     * se inicializa de nuevo. Nunca modifica una faena existente.
     */
    @Query("""
        INSERT INTO faenas (
            nombre, ubicacion, descripcion, fechaInicio, fechaTermino,
            activa, latitud, longitud
        )
        SELECT :nombre, 'Santiago', 'Faena inicial para pruebas de asistencia',
               '', '', 1, :latitud, :longitud
        WHERE NOT EXISTS (
            SELECT 1 FROM faenas WHERE nombre = :nombre COLLATE NOCASE
        )
    """)
    suspend fun insertarPredeterminadaSiNoExiste(
        nombre: String,
        latitud: Double,
        longitud: Double
    ): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarFaena(faena: Faena)

    @Update
    suspend fun actualizarFaena(faena: Faena)

    @Query("UPDATE faenas SET activa = :activa WHERE id = :id")
    suspend fun actualizarEstado(id: Long, activa: Boolean)
}
