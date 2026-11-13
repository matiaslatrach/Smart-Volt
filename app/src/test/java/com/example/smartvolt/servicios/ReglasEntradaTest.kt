package com.example.smartvolt.servicios

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasEntradaTest {
    @Test fun exigeTodosLosRequisitos() {
        assertTrue(ReglasEntrada.habilitada(true, true, true, true))
        assertFalse(ReglasEntrada.habilitada(false, true, true, true))
        assertFalse(ReglasEntrada.habilitada(true, false, true, true))
        assertFalse(ReglasEntrada.habilitada(true, true, false, true))
        assertFalse(ReglasEntrada.habilitada(true, true, true, false))
    }

    @Test fun consideraElMargenDePrecisionDentroDe100Metros() {
        assertTrue(ReglasEntrada.dentroDelRadio(70f, 30f))
        assertFalse(ReglasEntrada.dentroDelRadio(71f, 30f))
        assertFalse(ReglasEntrada.dentroDelRadio(90f, 15f))
        assertFalse(ReglasEntrada.dentroDelRadio(20f, 31f))
        assertFalse(ReglasEntrada.dentroDelRadio(Float.NaN, 5f))
    }
}
