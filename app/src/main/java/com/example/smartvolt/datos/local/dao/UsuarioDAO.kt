
package com.example.smartvolt.datos.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartvolt.modelo.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDAO {

    // Obtener un usuario por su ID.
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: String): Usuario?

    // Crear o reemplazar un usuario si ya existe.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarUsuario(usuario: Usuario)

    // Actualizar los datos de un usuario.
    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    // Eliminar un usuario.
    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)

    // Listar todos los usuarios.
    @Query("SELECT * FROM usuarios ORDER BY id ASC")
    fun listarUsuarios(): Flow<List<Usuario>>

    // Activar o desactivar un usuario.
    @Query("UPDATE usuarios SET activo = :activo WHERE id = :id")
    suspend fun actualizarEstadoUsuario(
        id: String,
        activo: Boolean
    )

    // Obtener operarios asignados a un supervisor.
    @Query("""
        SELECT * FROM usuarios
        WHERE supervisorId = :supervisorId
        AND rol = 'OPERARIO'
        ORDER BY nombreCompleto ASC
    """)
    fun listarPersonalPorSupervisor(
        supervisorId: String
    ): Flow<List<Usuario>>

    // Asignar o quitar una faena.
    @Query("UPDATE usuarios SET faenaId = :faenaId WHERE id = :usuarioId")
    suspend fun asignarFaena(
        usuarioId: String,
        faenaId: Long?
    )

    @Query("""
    SELECT * FROM usuarios
    WHERE id = :supervisorId
    AND rol = 'SUPERVISOR'
    AND activo = 1
    """)
    suspend fun obtenerSupervisorActivo(
        supervisorId: String
    ): Usuario?

    // Contar todos los operarios asignados a un supervisor.
    @Query("""
    SELECT COUNT(*) FROM usuarios
    WHERE supervisorId = :supervisorId
    AND rol = 'OPERARIO'
    """)
    suspend fun contarOperariosPorSupervisor(
        supervisorId: String
    ): Int

    // Contar solamente los operarios activos asignados a un supervisor.
    @Query("""
    SELECT COUNT(*) FROM usuarios
    WHERE supervisorId = :supervisorId
    AND rol = 'OPERARIO'
    AND activo = 1
    """)
    suspend fun contarOperariosActivosPorSupervisor(
        supervisorId: String
    ): Int
}
