package com.example.smartvolt.datos.local

import com.example.smartvolt.datos.local.dao.FaenaDAO

/** Faenas disponibles para probar el registro con GPS y Bluetooth DEMO. */
object FaenasIniciales {

    suspend fun registrarSiFaltan(faenaDAO: FaenaDAO) {
        faenaDAO.insertarPredeterminadaSiNoExiste(
            nombre = "DUOC PADRE ALONSO DE OVALLE",
            latitud = -33.446327,
            longitud = -70.657515
        )

        faenaDAO.insertarPredeterminadaSiNoExiste(
            nombre = "PLAZA CONSTITUCION",
            latitud = -33.441754,
            longitud = -70.654102
        )
    }
}
