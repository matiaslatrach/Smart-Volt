package com.example.smartvolt.datos.repositorio

import com.example.smartvolt.datos.local.dao.FaenaDAO
import com.example.smartvolt.modelo.Faena
import kotlinx.coroutines.flow.Flow

class FaenaRepositorio(
    private val faenaDAO: FaenaDAO
) {

    fun listarFaenas(): Flow<List<Faena>> {
        return faenaDAO.listarFaenas()
    }

    suspend fun agregarFaena(faena: Faena) {
        faenaDAO.agregarFaena(faena)
    }

    suspend fun actualizarFaena(faena: Faena) {
        faenaDAO.actualizarFaena(faena)
    }

    suspend fun actualizarEstado(id: Long, activa: Boolean) {
        faenaDAO.actualizarEstado(id, activa)
    }
}
