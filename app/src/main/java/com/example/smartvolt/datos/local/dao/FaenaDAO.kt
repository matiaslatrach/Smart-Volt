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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarFaena(faena: Faena)

    @Update
    suspend fun actualizarFaena(faena: Faena)

    @Query("UPDATE faenas SET activa = :activa WHERE id = :id")
    suspend fun actualizarEstado(id: Long, activa: Boolean)
}
