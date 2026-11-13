package com.example.smartvolt.servicios

/** Reglas independientes de Android; se pueden probar con JUnit. */
object ReglasEntrada {
    fun habilitada(
        bluetoothEncendido: Boolean,
        balizaDemoActivada: Boolean,
        gpsValido: Boolean,
        faenaActiva: Boolean
    ): Boolean = bluetoothEncendido && balizaDemoActivada && gpsValido && faenaActiva

    fun dentroDelRadio(distancia: Float, precision: Float): Boolean =
        distancia.isFinite() && precision.isFinite() &&
        distancia >= 0f && precision in 0f..30f && distancia + precision <= 100f
}
