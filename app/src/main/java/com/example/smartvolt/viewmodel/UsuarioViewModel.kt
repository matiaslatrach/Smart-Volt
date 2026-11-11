
package com.example.smartvolt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartvolt.datos.repositorio.UsuarioRepositorio
import com.example.smartvolt.datos.seguridad.GestorContrasenas
import com.example.smartvolt.modelo.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

data class UsuarioUiState(
    val usuarios: List<Usuario> = emptyList(),
    val personalACargo: List<Usuario> = emptyList(),
    val cargando: Boolean = false,
    val cargandoPersonal: Boolean = false,
    val usuarioActualizandoId: String? = null,
    val creandoUsuario: Boolean = false,
    val mensaje: String? = null,
    val mensajeError: String? = null,
    val mensajeErrorPersonal: String? = null
)

class UsuarioViewModel(
    private val repositorio: UsuarioRepositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private val bloqueoCreacion = Mutex()
    private val bloqueoActualizacion = Mutex()
    private val bloqueoEdicion = Mutex()

    init {
        observarUsuarios()
    }

    // --------------------------------------------------
    // LISTAR USUARIOS
    // --------------------------------------------------

    private fun observarUsuarios() {
        viewModelScope.launch {
            repositorio.listarUsuarios().collect { usuarios ->
                _uiState.value = _uiState.value.copy(
                    usuarios = usuarios
                )
            }
        }
    }

    // --------------------------------------------------
    // CREAR USUARIO
    // --------------------------------------------------

    fun crearUsuario(
        id: String,
        nombreCompleto: String,
        contrasena: String,
        cargo: String,
        rol: String,
        supervisorId: String? = null
    ) {
        viewModelScope.launch {
            if (!bloqueoCreacion.tryLock()) return@launch

            try {
                _uiState.value = _uiState.value.copy(
                    creandoUsuario = true,
                    mensaje = null,
                    mensajeError = null
                )

                val idLimpio = id.trim().uppercase()
                val nombreLimpio = nombreCompleto.trim()
                val cargoLimpio = cargo.trim()
                val rolLimpio = rol.trim().uppercase()

                val supervisorLimpio = supervisorId
                    ?.trim()
                    ?.uppercase()
                    ?.takeIf { it.isNotBlank() }

                // Validar campos obligatorios.
                if (
                    idLimpio.isBlank() ||
                    nombreLimpio.isBlank() ||
                    contrasena.isBlank() ||
                    cargoLimpio.isBlank() ||
                    rolLimpio.isBlank()
                ) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "Completa todos los campos obligatorios."
                    )
                    return@launch
                }

                // Validar longitud de contraseña.
                if (contrasena.length < 6) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "La contraseña debe tener al menos 6 caracteres."
                    )
                    return@launch
                }

                // Validar rol.
                val rolesPermitidos = listOf(
                    "OPERARIO",
                    "SUPERVISOR",
                    "ADMINISTRADOR"
                )

                if (rolLimpio !in rolesPermitidos) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "El rol seleccionado no es válido."
                    )
                    return@launch
                }

                // Comprobar que el ID no esté registrado.
                if (repositorio.existeUsuario(idLimpio)) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "Ya existe un usuario con ese ID."
                    )
                    return@launch
                }

                // Evitar que el usuario sea su propio supervisor.
                if (
                    rolLimpio == "OPERARIO" &&
                    supervisorLimpio == idLimpio
                ) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "Un usuario no puede ser su propio supervisor."
                    )
                    return@launch
                }

                // Validar supervisor cuando se proporciona.
                if (
                    rolLimpio == "OPERARIO" &&
                    supervisorLimpio != null
                ) {
                    val supervisor = repositorio.obtenerSupervisorActivo(
                        supervisorLimpio
                    )

                    if (supervisor == null) {
                        _uiState.value = _uiState.value.copy(
                            mensajeError = "El supervisor seleccionado no existe o está inactivo."
                        )
                        return@launch
                    }
                }

                // Generar hash seguro de la contraseña.
                val contrasenaHash = GestorContrasenas.generarHash(
                    contrasena
                )

                // Construir nuevo usuario.
                val nuevoUsuario = Usuario(
                    id = idLimpio,
                    contrasenaHash = contrasenaHash,
                    nombreCompleto = nombreLimpio,
                    cargo = cargoLimpio,
                    rol = rolLimpio,
                    activo = true,
                    supervisorId = if (rolLimpio == "OPERARIO") {
                        supervisorLimpio
                    } else {
                        null
                    },
                    faenaId = null
                )

                repositorio.crearUsuario(nuevoUsuario)

                _uiState.value = _uiState.value.copy(
                    mensaje = "Usuario creado correctamente.",
                    mensajeError = null
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible crear el usuario."
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    creandoUsuario = false
                )
                bloqueoCreacion.unlock()
            }
        }
    }

    // --------------------------------------------------
    // ACTUALIZAR ESTADO DEL USUARIO
    // --------------------------------------------------

    fun actualizarEstadoUsuario(
        id: String,
        activo: Boolean
    ) {
        viewModelScope.launch {
            if (!bloqueoActualizacion.tryLock()) return@launch

            try {
                _uiState.value = _uiState.value.copy(
                    usuarioActualizandoId = id,
                    mensaje = null,
                    mensajeError = null
                )

                // Obtener el usuario original desde la base de datos.
                val usuario = repositorio.obtenerUsuarioPorId(id)

                if (usuario == null) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "No se encontró el usuario seleccionado."
                    )
                    return@launch
                }

                // No permitir desactivar supervisores con operarios activos.
                if (
                    usuario.rol == "SUPERVISOR" &&
                    !activo
                ) {
                    val operariosActivos =
                        repositorio.contarOperariosActivosPorSupervisor(id)

                    if (operariosActivos > 0) {
                        _uiState.value = _uiState.value.copy(
                            mensajeError = "No puedes desactivar este supervisor porque tiene $operariosActivos operario(s) activo(s) asignado(s)."
                        )
                        return@launch
                    }
                }

                repositorio.actualizarEstadoUsuario(id, activo)

                _uiState.value = _uiState.value.copy(
                    mensaje = if (activo) {
                        "Usuario activado correctamente."
                    } else {
                        "Usuario desactivado correctamente."
                    },
                    mensajeError = null
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible actualizar el estado del usuario."
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    usuarioActualizandoId = null
                )
                bloqueoActualizacion.unlock()
            }
        }
    }

    // --------------------------------------------------
    // LISTAR PERSONAL A CARGO
    // --------------------------------------------------

    fun observarPersonalPorSupervisor(supervisorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargandoPersonal = true,
                mensajeErrorPersonal = null
            )

            try {
                repositorio.listarPersonalPorSupervisor(
                    supervisorId
                ).collect { personal ->
                    _uiState.value = _uiState.value.copy(
                        personalACargo = personal,
                        cargandoPersonal = false,
                        mensajeErrorPersonal = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargandoPersonal = false,
                    mensajeErrorPersonal = "No fue posible cargar el personal a cargo."
                )
            }
        }
    }

    // --------------------------------------------------
    // ASIGNAR FAENA A USUARIO
    // --------------------------------------------------

    fun asignarFaena(
        usuarioId: String,
        faenaId: Long?
    ) {
        viewModelScope.launch {
            try {
                repositorio.asignarFaena(
                    usuarioId = usuarioId,
                    faenaId = faenaId
                )

                _uiState.value = _uiState.value.copy(
                    mensaje = "Faena asignada correctamente.",
                    mensajeError = null
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible asignar la faena."
                )
            }
        }
    }

    // --------------------------------------------------
    // ACTUALIZAR USUARIO
    // --------------------------------------------------

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            if (!bloqueoEdicion.tryLock()) return@launch

            try {
                _uiState.value = _uiState.value.copy(
                    mensaje = null,
                    mensajeError = null
                )

                // Consultar el registro original.
                val usuarioOriginal =
                    repositorio.obtenerUsuarioPorId(usuario.id)

                if (usuarioOriginal == null) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "No se encontró el usuario que deseas editar."
                    )
                    return@launch
                }

                val nombreLimpio = usuario.nombreCompleto.trim()
                val cargoLimpio = usuario.cargo.trim()
                val rolLimpio = usuario.rol.trim().uppercase()

                // Validar campos obligatorios.
                if (
                    nombreLimpio.isBlank() ||
                    cargoLimpio.isBlank()
                ) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "El nombre y el cargo son obligatorios."
                    )
                    return@launch
                }

                // Validar rol.
                val rolesPermitidos = listOf(
                    "OPERARIO",
                    "SUPERVISOR",
                    "ADMINISTRADOR"
                )

                if (rolLimpio !in rolesPermitidos) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "El rol seleccionado no es válido."
                    )
                    return@launch
                }

                // No permitir cambiar el rol de un supervisor
                // mientras tenga operarios asignados.
                if (
                    usuarioOriginal.rol == "SUPERVISOR" &&
                    rolLimpio != "SUPERVISOR"
                ) {
                    val operariosAsignados =
                        repositorio.contarOperariosPorSupervisor(
                            usuarioOriginal.id
                        )

                    if (operariosAsignados > 0) {
                        _uiState.value = _uiState.value.copy(
                            mensajeError = "No puedes cambiar el rol de este supervisor porque tiene $operariosAsignados operario(s) asignado(s)."
                        )
                        return@launch
                    }
                }

                // Validar supervisor de operarios.
                val supervisorId = if (rolLimpio == "OPERARIO") {
                    usuario.supervisorId
                        ?.trim()
                        ?.uppercase()
                        ?.takeIf { it.isNotBlank() }
                } else {
                    null
                }

                // Evitar que un usuario sea su propio supervisor.
                if (supervisorId == usuarioOriginal.id) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "Un usuario no puede ser su propio supervisor."
                    )
                    return@launch
                }

                // Validar que el supervisor exista y esté activo.
                if (
                    rolLimpio == "OPERARIO" &&
                    supervisorId != null
                ) {
                    val supervisor = repositorio.obtenerSupervisorActivo(
                        supervisorId
                    )

                    if (supervisor == null) {
                        _uiState.value = _uiState.value.copy(
                            mensajeError = "El supervisor seleccionado no existe o está inactivo."
                        )
                        return@launch
                    }
                }

                // Construir usuario actualizado.
                // Se conservan contraseña, estado y faena originales.
                val usuarioActualizado = usuarioOriginal.copy(
                    nombreCompleto = nombreLimpio,
                    cargo = cargoLimpio,
                    rol = rolLimpio,
                    supervisorId = supervisorId
                )

                repositorio.actualizarUsuario(usuarioActualizado)

                _uiState.value = _uiState.value.copy(
                    mensaje = "Usuario actualizado correctamente.",
                    mensajeError = null
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible actualizar el usuario."
                )
            } finally {
                bloqueoEdicion.unlock()
            }
        }
    }

    // --------------------------------------------------
    // ELIMINAR USUARIO
    // --------------------------------------------------


    fun eliminarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    mensaje = null,
                    mensajeError = null
                )

                // Consultar el usuario directamente desde la base de datos.
                val usuarioOriginal =
                    repositorio.obtenerUsuarioPorId(usuario.id)

                if (usuarioOriginal == null) {
                    _uiState.value = _uiState.value.copy(
                        mensajeError = "No se encontró el usuario que deseas eliminar."
                    )
                    return@launch
                }

                // Proteger supervisores con operarios asignados.
                if (usuarioOriginal.rol == "SUPERVISOR") {
                    val operariosAsignados =
                        repositorio.contarOperariosPorSupervisor(
                            usuarioOriginal.id
                        )

                    if (operariosAsignados > 0) {
                        _uiState.value = _uiState.value.copy(
                            mensajeError = "No puedes eliminar este supervisor porque tiene $operariosAsignados operario(s) asignado(s). Reasigna primero a su personal."
                        )
                        return@launch
                    }
                }

                repositorio.eliminarUsuario(usuarioOriginal)

                _uiState.value = _uiState.value.copy(
                    mensaje = "Usuario eliminado correctamente.",
                    mensajeError = null
                )

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible eliminar el usuario."
                )
            }
        }
    }


    // --------------------------------------------------
    // LIMPIAR MENSAJES
    // --------------------------------------------------

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            mensaje = null,
            mensajeError = null
        )
    }
}
