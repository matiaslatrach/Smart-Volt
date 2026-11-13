package com.example.smartvolt.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faenas")
data class Faena(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nombre: String,
    val ubicacion: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaTermino: String,
    val activa: Boolean = true,
    val latitud: Double? = null,
    val longitud: Double? = null
)
