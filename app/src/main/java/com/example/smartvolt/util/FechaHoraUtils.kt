package com.example.smartvolt.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FechaHoraUtils {

    fun formatearFechaHora(fechaHora: Long): String {
        val formato = SimpleDateFormat(
            "dd/MM/yyyy - HH:mm",
            Locale.forLanguageTag("es-CL")
        )

        return formato.format(Date(fechaHora))
    }
}
