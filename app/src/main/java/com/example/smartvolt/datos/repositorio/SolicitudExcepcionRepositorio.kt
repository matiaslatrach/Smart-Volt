package com.example.smartvolt.datos.repositorio

import com.example.smartvolt.datos.local.dao.SolicitudExcepcionDAO
import com.example.smartvolt.modelo.SolicitudExcepcion

class SolicitudExcepcionRepositorio(private val dao: SolicitudExcepcionDAO) {
    fun pendientes(supervisorId: String) = dao.pendientes(supervisorId)
    fun porOperario(usuarioId: String) = dao.porOperario(usuarioId)
    suspend fun solicitar(solicitud: SolicitudExcepcion) = dao.solicitar(solicitud)
    suspend fun resolver(id: Long, supervisorId: String, aprobar: Boolean, nota: String) =
        dao.resolver(id, supervisorId, aprobar, nota)
}
