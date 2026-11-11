
package com.example.smartvolt.datos.repositorio

import com.example.smartvolt.datos.local.dao.UsuarioDAO
import com.example.smartvolt.modelo.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepositorio(
    private val usuarioDAO: UsuarioDAO
) {

    // Listar todos los usuarios.
    fun listarUsuarios(): Flow<List<Usuario>> {
        return usuarioDAO.listarUsuarios()
    }

    // Buscar un usuario por su ID.
    suspend fun obtenerUsuarioPorId(id: String): Usuario? {
        return usuarioDAO.obtenerUsuarioPorId(id)
    }

    // Comprobar si un ID ya está registrado.
    suspend fun existeUsuario(id: String): Boolean {
        return usuarioDAO.obtenerUsuarioPorId(id) != null
    }

    // Crear un nuevo usuario.
    suspend fun crearUsuario(usuario: Usuario) {
        usuarioDAO.agregarUsuario(usuario)
    }

    // Actualizar los datos de un usuario.
    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDAO.actualizarUsuario(usuario)
    }

    // Eliminar un usuario.
    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDAO.eliminarUsuario(usuario)
    }

    // Actualizar el estado de un usuario.
    suspend fun actualizarEstadoUsuario(
        id: String,
        activo: Boolean
    ) {
        usuarioDAO.actualizarEstadoUsuario(
            id = id,
            activo = activo
        )
    }

    // Obtener el personal a cargo de un supervisor.
    fun listarPersonalPorSupervisor(
        supervisorId: String
    ): Flow<List<Usuario>> {
        return usuarioDAO.listarPersonalPorSupervisor(
            supervisorId = supervisorId
        )
    }

    // Asignar o quitar una faena.
    suspend fun asignarFaena(
        usuarioId: String,
        faenaId: Long?
    ) {
        usuarioDAO.asignarFaena(
            usuarioId = usuarioId,
            faenaId = faenaId
        )
    }

    // Obtener un supervisor activo por su ID.
    suspend fun obtenerSupervisorActivo(
        supervisorId: String
    ): Usuario? {
        return usuarioDAO.obtenerSupervisorActivo(supervisorId)
    }

    // Contar todos los operarios asignados a un supervisor.
    suspend fun contarOperariosPorSupervisor(
        supervisorId: String
    ): Int {
        return usuarioDAO.contarOperariosPorSupervisor(supervisorId)
    }

    // Contar solamente los operarios activos.
    suspend fun contarOperariosActivosPorSupervisor(
        supervisorId: String
    ): Int {
        return usuarioDAO.contarOperariosActivosPorSupervisor(supervisorId)
    }
}
