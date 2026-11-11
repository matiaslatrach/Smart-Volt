package com.example.smartvolt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartvolt.datos.repositorio.AsistenciaRepositorio
import com.example.smartvolt.modelo.Asistencia
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

data class AsistenciaUiState(
    val historial: List<Asistencia> = emptyList(),
    val ultimaAsistencia: Asistencia? = null,
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val mensajeError: String? = null
)

class AsistenciaViewModel(
    private val repositorio: AsistenciaRepositorio,
    private val usuarioId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AsistenciaUiState())
    val uiState: StateFlow<AsistenciaUiState> = _uiState.asStateFlow()

    // Impide que se ejecuten marcaciones simultáneas.
    private val bloqueoMarcacion = Mutex()

    init {
        observarHistorial()
    }

    // Observar los cambios en el historial del usuario.
    private fun observarHistorial() {
        viewModelScope.launch {
            repositorio.listarAsistenciasPorUsuario(usuarioId)
                .collect { asistencias ->
                    _uiState.value = _uiState.value.copy(
                        historial = asistencias,
                        ultimaAsistencia = asistencias.firstOrNull()
                    )
                }
        }
    }

    // Registrar la siguiente marcación mediante Room.
    fun registrarMarcacion() {

        // Si ya existe una operación, ignorar la nueva pulsación.
        if (!bloqueoMarcacion.tryLock()) return

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensaje = null,
                mensajeError = null
            )

            try {
                // Room determina y registra la siguiente marcación
                // dentro de una única transacción.
                val asistencia = repositorio.registrarSiguienteMarcacion(
                    usuarioId = usuarioId,
                    fechaHora = System.currentTimeMillis()
                )

                _uiState.value = _uiState.value.copy(
                    mensaje = if (asistencia.tipo == "ENTRADA") {
                        "Entrada registrada correctamente."
                    } else {
                        "Salida registrada correctamente."
                    },
                    mensajeError = null
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    mensaje = null,
                    mensajeError = "No fue posible registrar la asistencia."
                )

            } finally {

                // Liberar el estado de carga y el bloqueo.
                _uiState.value = _uiState.value.copy(
                    cargando = false
                )

                bloqueoMarcacion.unlock()
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            mensaje = null,
            mensajeError = null
        )
    }
}
