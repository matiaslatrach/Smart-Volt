package com.example.smartvolt.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(

    @PrimaryKey
    val id: String,

    val contrasenaHash: String,

    val nombreCompleto: String,

    val cargo: String,

    val rol: String,

    val activo: Boolean = true,

    val supervisorId: String? = null,

    val faenaId: Long? = null
)
