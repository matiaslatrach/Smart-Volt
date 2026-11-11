package com.example.smartvolt.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asistencias",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId"])
    ]
)
data class Asistencia(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val usuarioId: String,

    val fechaHora: Long,

    val tipo: String,
    val faenaId: Long? = null,
    val metodoRegistro: String = "LEGADO",
    val validacionBluetooth: String = "NO_VERIFICADO",
    val validacionGps: String = "NO_VERIFICADO",
    val distanciaMetros: Double? = null,
    val precisionMetros: Float? = null,
    val solicitudId: Long? = null
)
