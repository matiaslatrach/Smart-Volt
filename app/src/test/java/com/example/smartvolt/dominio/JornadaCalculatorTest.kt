package com.example.smartvolt.dominio

import com.example.smartvolt.modelo.Asistencia
import org.junit.Assert.*
import org.junit.Test

class JornadaCalculatorTest {
    @Test fun jornadaNocturnaEnUnaSolaFila() {
        val inicio = Asistencia(id = 1, usuarioId = "OP-001", fechaHora = 0L, tipo = "ENTRADA")
        val termino = Asistencia(id = 2, usuarioId = "OP-001", fechaHora = 8 * 60 * 60 * 1000L, tipo = "SALIDA")
        val filas = JornadaCalculator.agrupar(listOf(termino, inicio))
        assertEquals(1, filas.size)
        assertEquals(8 * 60 * 60 * 1000L, filas.first().duracionMs)
    }

    @Test fun entradaSinSalidaPermaneceAbierta() {
        val filas = JornadaCalculator.agrupar(listOf(Asistencia(usuarioId = "OP-001", fechaHora = 123L, tipo = "ENTRADA")))
        assertNull(filas.first().salida)
        assertNull(filas.first().duracionMs)
    }
}
