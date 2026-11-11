package com.example.smartvolt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartvolt.datos.repositorio.FaenaRepositorio
import com.example.smartvolt.modelo.Faena
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FaenaUiState(
    val faenas: List<Faena> = emptyList(),
    val cargando: Boolean = true,
    val mensaje: String? = null,
    val mensajeError: String? = null
)

class FaenaViewModel(
    private val repositorio: FaenaRepositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaenaUiState())
    val uiState: StateFlow<FaenaUiState> = _uiState.asStateFlow()

    init {
        observarFaenas()
    }

    private fun observarFaenas() {
        viewModelScope.launch {
            try {
                repositorio.listarFaenas().collect { faenas ->
                    _uiState.value = _uiState.value.copy(
                        faenas = faenas,
                        cargando = false,
                        mensajeError = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensajeError = "No fue posible cargar las faenas."
                )
            }
        }
    }

    fun guardarFaena(faena: Faena) {
        viewModelScope.launch {
            try {
                if (faena.id == 0L) {
                    repositorio.agregarFaena(faena)
                    _uiState.value = _uiState.value.copy(
                        mensaje = "Faena creada correctamente."
                    )
                } else {
                    repositorio.actualizarFaena(faena)
                    _uiState.value = _uiState.value.copy(
                        mensaje = "Faena actualizada correctamente."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible guardar la faena."
                )
            }
        }
    }

    fun cambiarEstado(faena: Faena) {
        viewModelScope.launch {
            try {
                repositorio.actualizarEstado(
                    id = faena.id,
                    activa = !faena.activa
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensajeError = "No fue posible cambiar el estado."
                )
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
