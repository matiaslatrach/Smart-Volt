
package com.example.smartvolt.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartvolt.ui.view.login.LoginView
import com.example.smartvolt.ui.view.operario.InicioOperarioView
import com.example.smartvolt.ui.view.supervisor.InicioSupervisorView
import com.example.smartvolt.ui.view.administrador.InicioAdministradorView
import com.example.smartvolt.viewmodel.LoginViewModel

@Composable
fun NavegacionPrincipal(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    val contexto = LocalContext.current

    val estadoLogin by loginViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // Login
        composable(Rutas.LOGIN) {
            LoginView(
                viewModel = loginViewModel,
                onLoginExitoso = { usuario ->
                    val rutaDestino = when (usuario.rol.trim().uppercase()) {
                        "OPERARIO" -> Rutas.INICIO_OPERARIO
                        "SUPERVISOR" -> Rutas.INICIO_SUPERVISOR
                        "ADMINISTRADOR" -> Rutas.INICIO_ADMINISTRADOR
                        else -> null
                    }

                    if (rutaDestino != null) {
                        navController.navigate(rutaDestino) {
                            popUpTo(Rutas.LOGIN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        Toast.makeText(
                            contexto,
                            "El usuario no tiene un rol válido.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        // Inicio del operario
        composable(Rutas.INICIO_OPERARIO) {
            val usuario = estadoLogin.usuarioLogeado

            if (usuario != null) {
                InicioOperarioView(
                    usuario = usuario,
                    onCerrarSesion = {
                        loginViewModel.cerrarSesion()

                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.INICIO_OPERARIO) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // Inicio del supervisor
        composable(Rutas.INICIO_SUPERVISOR) {
            val usuario = estadoLogin.usuarioLogeado

            if (usuario != null) {
                InicioSupervisorView(
                    usuario = usuario,
                    onCerrarSesion = {
                        loginViewModel.cerrarSesion()

                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(Rutas.INICIO_SUPERVISOR) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // Inicio del administrador
        composable(Rutas.INICIO_ADMINISTRADOR) {
            InicioAdministradorView(
                onCerrarSesion = {
                    loginViewModel.cerrarSesion()

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.INICIO_ADMINISTRADOR) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
