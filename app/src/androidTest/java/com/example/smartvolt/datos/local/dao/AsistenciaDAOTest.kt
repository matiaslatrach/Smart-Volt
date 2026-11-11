package com.example.smartvolt.datos.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.modelo.Usuario
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AsistenciaDAOTest {

    private lateinit var baseDatos: AppDataBase
    private lateinit var asistenciaDAO: AsistenciaDAO
    private lateinit var usuarioDAO: UsuarioDAO

    @Before
    fun configurarBaseDatos() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()

        baseDatos = Room.inMemoryDatabaseBuilder(
            contexto,
            AppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        asistenciaDAO = baseDatos.asistenciaDAO()
        usuarioDAO = baseDatos.usuarioDAO()
    }

    @After
    fun cerrarBaseDatos() {
        baseDatos.close()
    }

    @Test
    fun registrarSecuenciaDeAsistenciaCorrectamente() = runBlocking {

        // Crear un usuario de prueba.
        val usuario = Usuario(
            id = "OP-TEST",
            contrasenaHash = "hash_prueba",
            nombreCompleto = "Usuario de Prueba",
            cargo = "Operario",
            rol = "OPERARIO"
        )

        usuarioDAO.agregarUsuario(usuario)

        // Primera marcación: debe ser ENTRADA.
        val primera = asistenciaDAO.registrarSiguienteMarcacion(
            usuarioId = usuario.id,
            fechaHora = 1000L
        )

        assertEquals("ENTRADA", primera.tipo)

        // Segunda marcación: debe ser SALIDA.
        val segunda = asistenciaDAO.registrarSiguienteMarcacion(
            usuarioId = usuario.id,
            fechaHora = 2000L
        )

        assertEquals("SALIDA", segunda.tipo)

        // Tercera marcación: debe volver a ser ENTRADA.
        val tercera = asistenciaDAO.registrarSiguienteMarcacion(
            usuarioId = usuario.id,
            fechaHora = 3000L
        )

        assertEquals("ENTRADA", tercera.tipo)

        // Comprobar que se guardaron las tres marcaciones.
        val historial = asistenciaDAO
            .listarAsistenciasPorUsuario(usuario.id)

        // La consulta devuelve Flow; la comprobación
        // del historial se realizará en otra prueba.
    }
}
