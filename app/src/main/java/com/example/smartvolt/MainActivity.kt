
package com.example.smartvolt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.local.FaenasIniciales
import com.example.smartvolt.datos.seguridad.GestorContrasenas
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.navigation.NavegacionPrincipal
import com.example.smartvolt.ui.view.splash.SplashSmartVoltView
import com.example.smartvolt.ui.theme.SmartVoltTheme
import com.example.smartvolt.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDataBase.obtenerBaseDatos(applicationContext)
        val usuarioDAO = database.usuarioDAO()

        val loginViewModel = LoginViewModel(usuarioDAO)

        lifecycleScope.launch(Dispatchers.IO) {

            // Incorporar las dos faenas de demostración sin borrar ni duplicar datos.
            FaenasIniciales.registrarSiFaltan(database.faenaDAO())

            // =================================
            // USUARIO OPERARIO
            // =================================

            val usuarioExistente =
                usuarioDAO.obtenerUsuarioPorId("OP-001")

            if (usuarioExistente == null) {
                val usuarioPrueba = Usuario(
                    id = "OP-001",
                    contrasenaHash =
                        GestorContrasenas.generarHash("123456"),
                    nombreCompleto = "Juan Pérez",
                    cargo = "Operador",
                    rol = "OPERARIO",
                    activo = true
                )

                usuarioDAO.agregarUsuario(usuarioPrueba)
            }

            // =================================
            // USUARIO ADMINISTRADOR
            // =================================

            val administradorExistente =
                usuarioDAO.obtenerUsuarioPorId("AD-001")

            if (administradorExistente == null) {
                val administradorPrueba = Usuario(
                    id = "AD-001",
                    contrasenaHash =
                        GestorContrasenas.generarHash("123456"),
                    nombreCompleto = "Administrador SmartVolt",
                    cargo = "Administrador del sistema",
                    rol = "ADMINISTRADOR",
                    activo = true
                )

                usuarioDAO.agregarUsuario(administradorPrueba)
            }

            // =================================
            // USUARIO SUPERVISOR
            // =================================

            val supervisorExistente =
                usuarioDAO.obtenerUsuarioPorId("SU-001")

            if (supervisorExistente == null) {
                val supervisorPrueba = Usuario(
                    id = "SU-001",
                    contrasenaHash =
                        GestorContrasenas.generarHash("123456"),
                    nombreCompleto = "Sol Isla",
                    cargo = "Supervisor faena",
                    rol = "SUPERVISOR",
                    activo = true
                )

                usuarioDAO.agregarUsuario(supervisorPrueba)
            }

            // =================================
            // ASIGNAR OPERARIO AL SUPERVISOR
            // =================================

            val operario = usuarioDAO.obtenerUsuarioPorId("OP-001")

            if (operario != null && operario.supervisorId != "SU-001") {
                usuarioDAO.actualizarUsuario(
                    operario.copy(
                        supervisorId = "SU-001"
                    )
                )
            }
        }

        setContent {
            SmartVoltTheme {
                val navController = rememberNavController()

                // El splash aparece una vez al abrir la app, sin cambiar las rutas.
                var mostrarSplash by rememberSaveable { mutableStateOf(true) }

                if (mostrarSplash) {
                    SplashSmartVoltView(onFinalizar = { mostrarSplash = false })
                } else {
                    NavegacionPrincipal(
                        navController = navController,
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }
}
