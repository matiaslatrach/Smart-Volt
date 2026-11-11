package com.example.smartvolt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartvolt.datos.repositorio.AsistenciaRepositorio

class AsistenciaViewModelFactory(
    private val repositorio: AsistenciaRepositorio,
    private val usuarioId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(AsistenciaViewModel::class.java)) {
            return AsistenciaViewModel(
                repositorio = repositorio,
                usuarioId = usuarioId
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
