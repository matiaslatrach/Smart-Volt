
package com.example.smartvolt.datos.repositorio

import com.example.smartvolt.datos.local.dao.AsistenciaDAO
import com.example.smartvolt.modelo.Asistencia
import kotlinx.coroutines.flow.Flow

class AsistenciaRepositorio(
    private val asistenciaDAO: AsistenciaDAO
) {

    suspend fun registrarAsistencia(
        asistencia: Asistencia
    ): Long {
        return asistenciaDAO.registrarAsistencia(asistencia)
    }

    suspend fun registrarSiguienteMarcacion(
        usuarioId: String,
        fechaHora: Long
    ): Asistencia {
        return asistenciaDAO.registrarSiguienteMarcacion(
            usuarioId = usuarioId,
            fechaHora = fechaHora
        )
    }

    fun listarAsistenciasPorUsuario(
        usuarioId: String
    ): Flow<List<Asistencia>> {
        return asistenciaDAO.listarAsistenciasPorUsuario(usuarioId)
    }

    suspend fun obtenerUltimaAsistencia(
        usuarioId: String
    ): Asistencia? {
        return asistenciaDAO.obtenerUltimaAsistencia(usuarioId)
    }

    fun listarAsistenciasPorSupervisor(
        supervisorId: String
    ): Flow<List<Asistencia>> {
        return asistenciaDAO.listarAsistenciasPorSupervisor(supervisorId)
    }
}
