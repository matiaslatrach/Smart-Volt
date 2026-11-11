package com.example.smartvolt.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

/** La aprobación local solo sirve en una instalación compartida para demostración. */
@Entity(tableName = "solicitudes_excepcion")
data class SolicitudExcepcion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: String,
    val supervisorId: String,
    val faenaId: Long,
    val tipo: String,
    val fechaSolicitud: Long,
    val motivo: String,
    val bluetoothValido: Boolean,
    val gpsValido: Boolean,
    val estado: String = "PENDIENTE",
    val fechaResolucion: Long? = null,
    val observacion: String? = null
)
