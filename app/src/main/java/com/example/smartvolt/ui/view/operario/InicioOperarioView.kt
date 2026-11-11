
package com.example.smartvolt.ui.view.operario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.AsistenciaRepositorio
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.components.PanelPrincipalView
import com.example.smartvolt.viewmodel.AsistenciaViewModel
import com.example.smartvolt.viewmodel.AsistenciaViewModelFactory

@Composable
fun InicioOperarioView(
    usuario: Usuario,
    onCerrarSesion: () -> Unit
) {
    val contexto = LocalContext.current

    val baseDatos = remember(contexto) {
        AppDataBase.obtenerBaseDatos(contexto)
    }

    val repositorio = remember(baseDatos) {
        AsistenciaRepositorio(baseDatos.asistenciaDAO())
    }

    val factory = remember(repositorio, usuario.id) {
        AsistenciaViewModelFactory(
            repositorio = repositorio,
            usuarioId = usuario.id
        )
    }

    val asistenciaViewModel: AsistenciaViewModel = viewModel(
        key = usuario.id,
        factory = factory
    )

    var faenaAsignada by remember(usuario.id) {
        mutableStateOf<Faena?>(null)
    }

    var cargandoFaena by remember(usuario.id) {
        mutableStateOf(false)
    }

    var errorFaena by remember(usuario.id) {
        mutableStateOf(false)
    }

    LaunchedEffect(usuario.id, usuario.faenaId) {
        val faenaId = usuario.faenaId

        faenaAsignada = null
        errorFaena = false

        if (faenaId != null) {
            cargandoFaena = true

            try {
                faenaAsignada = baseDatos.faenaDAO().obtenerFaena(faenaId)
            } catch (e: Exception) {
                errorFaena = true
            } finally {
                cargandoFaena = false
            }
        } else {
            cargandoFaena = false
        }
    }

    PanelPrincipalView(
        nombreRol = "OPERARIO",
        titulo = "Bienvenido al área operativa",
        subtitulo = "Gestiona tu jornada y consulta tu asistencia.",
        modulos = listOf(
            "Marcar asistencia",
            "Mi jornada laboral",
            "Historial de asistencia",
            "Mis notificaciones"
        ),
        onCerrarSesion = onCerrarSesion,

        contenidoPersonalizado = { indice, cambiarSeccion ->
            when (indice) {
                0 -> ContenidoInicioOperario(
                    faena = faenaAsignada,
                    cargandoFaena = cargandoFaena,
                    errorFaena = errorFaena,
                    onCambiarSeccion = cambiarSeccion
                )

                1 -> AsistenciaOperarioView(
                    viewModel = asistenciaViewModel
                )

                2 -> HistorialOperarioView(
                    viewModel = asistenciaViewModel
                )

                3 -> PerfilOperarioView(usuario)
            }
        }
    )
}

@Composable
private fun ContenidoInicioOperario(
    faena: Faena?,
    cargandoFaena: Boolean,
    errorFaena: Boolean,
    onCambiarSeccion: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "SMARTVOLT",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Bienvenido al área operativa",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Gestiona tu jornada y consulta tu asistencia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Información de la faena asignada.
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Mi faena asignada",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when {
                        cargandoFaena -> {
                            CircularProgressIndicator()
                            Text(
                                text = "Consultando tu faena...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        errorFaena -> {
                            Text(
                                text = "No fue posible consultar tu faena. Intenta nuevamente más tarde.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        faena == null -> {
                            Text(
                                text = "Sin faena asignada",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Actualmente no tienes una faena asignada. Contacta a tu supervisor o administrador.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        else -> {
                            Text(
                                text = faena.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Ubicación: ${faena.ubicacion}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (faena.descripcion.isNotBlank()) {
                                Text(
                                    text = faena.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "Inicio: ${faena.fechaInicio}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Término: ${faena.fechaTermino}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = if (faena.activa) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.errorContainer
                                }
                            ) {
                                Text(
                                    text = if (faena.activa) "Faena activa" else "Faena finalizada",
                                    modifier = Modifier.padding(
                                        horizontal = 14.dp,
                                        vertical = 7.dp
                                    ),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (faena.activa) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onErrorContainer
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        val accesos = listOf(
            "Control de asistencia",
            "Historial de jornadas",
            "Mi perfil"
        )

        accesos.forEachIndexed { indice, acceso ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCambiarSeccion(indice + 1)
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = acceso,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
