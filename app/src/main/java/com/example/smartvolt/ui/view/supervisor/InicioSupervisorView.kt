package com.example.smartvolt.ui.view.supervisor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.UsuarioRepositorio
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.components.PanelPrincipalView
import com.example.smartvolt.ui.theme.AzulMedio
import com.example.smartvolt.ui.theme.AzulOscuro
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.viewmodel.UsuarioViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModelFactory
import com.example.smartvolt.datos.repositorio.AsistenciaRepositorio
import com.example.smartvolt.modelo.Asistencia
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.util.FechaHoraUtils


@Composable
fun InicioSupervisorView(
    usuario: Usuario,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current

    val usuarioDAO = remember {
        AppDataBase.obtenerBaseDatos(context).usuarioDAO()
    }

    val repositorio = remember(usuarioDAO) {
        UsuarioRepositorio(usuarioDAO)
    }

    val factory = remember(repositorio) {
        UsuarioViewModelFactory(repositorio)
    }

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = factory
    )

    val estado by usuarioViewModel.uiState.collectAsState()

    // Consultar las faenas asignadas al personal del supervisor.
    val faenaDAO = remember {
        AppDataBase.obtenerBaseDatos(context).faenaDAO()
    }

    var faenasPorUsuario by remember {
        mutableStateOf<Map<String, Faena>>(emptyMap())
    }

    LaunchedEffect(estado.personalACargo) {
        val resultado = mutableMapOf<String, Faena>()

        estado.personalACargo.forEach { trabajador ->
            trabajador.faenaId?.let { faenaId ->
                try {
                    val faena = faenaDAO.obtenerFaena(faenaId)

                    if (faena != null) {
                        resultado[trabajador.id] = faena
                    }
                } catch (e: Exception) {
                    // Si una consulta falla, se conserva el resto del equipo.
                }
            }
        }

        faenasPorUsuario = resultado
    }

    val asistenciaDAO = remember {
        AppDataBase.obtenerBaseDatos(context).asistenciaDAO()
    }

    val asistenciaRepositorio = remember(asistenciaDAO) {
        AsistenciaRepositorio(asistenciaDAO)
    }

    val asistencias by asistenciaRepositorio
        .listarAsistenciasPorSupervisor(usuario.id)
        .collectAsState(initial = emptyList())


    LaunchedEffect(usuario.id) {
        usuarioViewModel.observarPersonalPorSupervisor(usuario.id)
    }

    PanelPrincipalView(
        nombreRol = "SUPERVISOR",
        titulo = "Bienvenido, ${usuario.nombreCompleto}",
        subtitulo = "Controla la asistencia y las actividades de tu equipo en faena.",
        modulos = listOf(
            "Asistencia del equipo",
            "Personal a cargo",
            "Revisión de jornadas",
            "Reportes de actividad",
            "Notificaciones"
        ),
        onCerrarSesion = onCerrarSesion,
        contenidoPersonalizado = { indice, cambiarSeccion ->
            when (indice) {
                0 -> ContenidoInicioSupervisor(
                    onCambiarSeccion = cambiarSeccion
                )

                1 -> SeccionPersonalACargo(
                    personal = estado.personalACargo,
                    cargando = estado.cargandoPersonal,
                    mensajeError = estado.mensajeErrorPersonal,
                    faenasPorUsuario = faenasPorUsuario
                )

                2 -> SeccionAsistenciaEquipo(
                    personal = estado.personalACargo,
                    asistencias = asistencias
                )

                3 -> SeccionReportesEquipo(
                    personal = estado.personalACargo,
                    asistencias = asistencias
                )
            }
        }
    )
}

@Composable
fun ContenidoInicioSupervisor(
    onCambiarSeccion: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "GESTIÓN DE EQUIPO",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Accesos de supervisión",
                color = AzulOscuro,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        val accesos = listOf(
            Triple("Personal a cargo", "Revisa la lista de trabajadores asignados", 1),
            Triple("Asistencia del equipo", "Supervisa entradas y salidas en tiempo real", 2),
            Triple("Reportes de actividad", "Consulta informes de jornadas completadas", 3)
        )

        accesos.forEach { (titulo, descripcion, destinoIndice) ->
            TarjetaAccesoSupervisor(
                titulo = titulo,
                descripcion = descripcion,
                onClick = { onCambiarSeccion(destinoIndice) }
            )
        }
    }
}

@Composable
fun TarjetaAccesoSupervisor(
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Borde)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                color = AzulOscuro,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "→",
                    color = VerdeVolt,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun SeccionSupervisor(
    titulo: String,
    descripcion: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "SUPERVISIÓN",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = titulo,
                color = AzulOscuro,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = descripcion,
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Borde)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = AzulMedio,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Funcionalidad en preparación",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeVolt
                    )
                }

                Text(
                    text = "Este módulo estará disponible próximamente para coordinar el trabajo en faena.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }
    }
}

@Composable
fun SeccionPersonalACargo(
    personal: List<Usuario>,
    cargando: Boolean,
    mensajeError: String?,
    faenasPorUsuario: Map<String, Faena>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "EQUIPO DE TRABAJO",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Personal a cargo",
                color = AzulOscuro,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Trabajadores y operarios asignados a tu supervisión.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterHorizontally),
                color = AzulOscuro,
                strokeWidth = 3.dp
            )
        } else if (mensajeError != null) {
            Surface(
                color = Color(0xFFFFEEEE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mensajeError,
                    color = Color(0xFFBA3030),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else if (personal.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Borde)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Sin trabajadores asignados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )

                    Text(
                        text = "Cuando se asignen operarios a tu equipo, aparecerán listados en esta sección.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }
        } else {
            Surface(
                color = AzulMedio,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${personal.size} trabajadores asignados",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerdeVolt,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                personal.forEach { trabajador ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Borde)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trabajador.nombreCompleto,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulOscuro
                                )

                                Surface(
                                    color = if (trabajador.activo) Color(0xFFEBFBEE) else Color(0xFFFFEEEE),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (trabajador.activo) "ACTIVO" else "INACTIVO",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (trabajador.activo) Color(0xFF27834A) else Color(0xFFBA3030)
                                    )
                                }
                            }

                            Text(
                                text = "Cargo: ${trabajador.cargo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )

                            HorizontalDivider(color = Borde, thickness = 1.dp)

                            Text(
                                text = "ID: ${trabajador.id}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextoSecundario

                            )
                            HorizontalDivider(color = Borde)

                            val faena = faenasPorUsuario[trabajador.id]

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "FAENA ASIGNADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoSecundario
                                )

                                Text(
                                    text = when {
                                        trabajador.faenaId == null -> "Sin faena asignada"
                                        faena != null -> faena.nombre
                                        else -> "Faena no disponible"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulOscuro
                                )

                                if (faena != null) {
                                    Text(
                                        text = "Ubicación: ${faena.ubicacion}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundario
                                    )

                                    Text(
                                        text = if (faena.activa) {
                                            "Estado: Faena activa"
                                        } else {
                                            "Estado: Faena finalizada"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (faena.activa) {
                                            Color(0xFF27834A)
                                        } else {
                                            Color(0xFFBA3030)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SeccionAsistenciaEquipo(
        personal: List<Usuario>,
        asistencias: List<Asistencia>
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "CONTROL DE JORNADA",
                    color = TextoSecundario,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Asistencia del equipo",
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Registro de entradas y salidas de tus trabajadores.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (asistencias.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Borde)
                ) {
                    Text(
                        text = "Todavía no existen marcaciones registradas para tu equipo.",
                        modifier = Modifier.padding(18.dp),
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Surface(
                    color = AzulMedio,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${asistencias.size} marcaciones registradas",
                        color = VerdeVolt,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                    )
                }

                asistencias.forEach { asistencia ->
                    val trabajador = personal.firstOrNull {
                        it.id == asistencia.usuarioId
                    }

                    val esEntrada = asistencia.tipo == "ENTRADA"
                    val fecha = remember(asistencia.fechaHora) {
                        FechaHoraUtils.formatearFechaHora(asistencia.fechaHora)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Borde)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = trabajador?.nombreCompleto
                                            ?: asistencia.usuarioId,
                                        color = AzulOscuro,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = "ID: ${asistencia.usuarioId}",
                                        color = TextoSecundario,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                                Surface(
                                    color = if (esEntrada) {
                                        Color(0xFFEBFBEE)
                                    } else {
                                        Color(0xFFFFEEEE)
                                    },
                                    shape = RoundedCornerShape(7.dp)
                                ) {
                                    Text(
                                        text = asistencia.tipo,
                                        color = if (esEntrada) {
                                            Color(0xFF27834A)
                                        } else {
                                            Color(0xFFBA3030)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 6.dp
                                        )
                                    )
                                }
                            }

                            HorizontalDivider(color = Borde)

                            Text(
                                text = fecha,
                                color = TextoSecundario,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SeccionReportesEquipo(
        personal: List<Usuario>,
        asistencias: List<Asistencia>
    ) {
        val ultimaMarcacionPorUsuario = asistencias
            .groupBy { it.usuarioId }
            .mapValues { (_, marcas) ->
                marcas.maxWithOrNull(
                    compareBy<Asistencia> { it.fechaHora }
                        .thenBy { it.id }
                )
            }

        val trabajadoresEnJornada = ultimaMarcacionPorUsuario.values.count {
            it?.tipo == "ENTRADA"
        }

        val trabajadoresFueraDeJornada = ultimaMarcacionPorUsuario.values.count {
            it?.tipo == "SALIDA"
        }

        val totalEntradas = asistencias.count {
            it.tipo == "ENTRADA"
        }

        val totalSalidas = asistencias.count {
            it.tipo == "SALIDA"
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "RESUMEN OPERACIONAL",
                    color = TextoSecundario,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Reportes de actividad",
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Resumen de las marcaciones registradas por tu equipo.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaResumenSupervisor(
                    titulo = "Personal asignado",
                    valor = personal.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                TarjetaResumenSupervisor(
                    titulo = "En jornada",
                    valor = trabajadoresEnJornada.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaResumenSupervisor(
                    titulo = "Entradas",
                    valor = totalEntradas.toString(),
                    modifier = Modifier.weight(1f)
                )

                TarjetaResumenSupervisor(
                    titulo = "Salidas",
                    valor = totalSalidas.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Borde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Estado actual del equipo",
                        color = AzulOscuro,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "$trabajadoresEnJornada trabajadores con última marcación de entrada.",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "$trabajadoresFueraDeJornada trabajadores con última marcación de salida.",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "${personal.size - ultimaMarcacionPorUsuario.size} trabajadores sin marcaciones.",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Text(
                text = "Nota: los totales de entradas y salidas corresponden a todas las marcaciones históricas disponibles, no solamente al día actual.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    @Composable
    private fun TarjetaResumenSupervisor(
        titulo: String,
        valor: String,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Borde),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = titulo,
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = valor,
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }


