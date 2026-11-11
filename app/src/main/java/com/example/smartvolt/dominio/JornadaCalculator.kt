package com.example.smartvolt.dominio

import com.example.smartvolt.modelo.Asistencia
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class JornadaResumen(
    val entrada: Asistencia,
    val salida: Asistencia?,
    val duracionMs: Long?,
    val incidencia: Boolean = false
)

object JornadaCalculator {
    /** Conserva registros originales. Una entrada sin salida se deja abierta. */
    fun agrupar(marcaciones: List<Asistencia>): List<JornadaResumen> {
        val ordenadas = marcaciones.sortedWith(compareBy<Asistencia> { it.fechaHora }.thenBy { it.id })
        val jornadas = mutableListOf<JornadaResumen>()
        var entrada: Asistencia? = null
        for (marca in ordenadas) {
            when (marca.tipo) {
                "ENTRADA" -> {
                    if (entrada != null) jornadas += JornadaResumen(entrada, null, null, incidencia = true)
                    entrada = marca
                }
                "SALIDA" -> {
                    val inicio = entrada
                    if (inicio != null) {
                        val ms = marca.fechaHora - inicio.fechaHora
                        jornadas += JornadaResumen(inicio, marca, ms.takeIf { it >= 0 }, incidencia = ms < 0)
                        entrada = null
                    }
                }
            }
        }
        entrada?.let { jornadas += JornadaResumen(it, null, null) }
        return jornadas.asReversed()
    }

    fun formatoDuracion(ms: Long?): String = if (ms == null) "En curso" else
        "${ms / 3_600_000} h ${(ms % 3_600_000) / 60_000} min"

    fun formatoHora(ms: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
    fun formatoFecha(ms: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ms))
}
