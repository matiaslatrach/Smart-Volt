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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.AsistenciaRepositorio
import com.example.smartvolt.datos.repositorio.UsuarioRepositorio
import com.example.smartvolt.modelo.Asistencia
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.components.PanelPrincipalView
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.BosqueProfundo
import com.example.smartvolt.ui.theme.EstadoError
import com.example.smartvolt.ui.theme.EstadoExitoso
import com.example.smartvolt.ui.theme.Fondo
import com.example.smartvolt.ui.theme.SuperficieSuave
import com.example.smartvolt.ui.theme.TextoPrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.util.FechaHoraUtils
import com.example.smartvolt.viewmodel.UsuarioViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModelFactory

private val BlancoSupervisor = Color.White

@Composable
fun InicioSupervisorView(
    usuario: Usuario,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current

    val baseDatos = remember {
        AppDataBase.obtenerBaseDatos(context)
    }

    val usuarioDAO = remember {
        baseDatos.usuarioDAO()
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

    val faenaDAO = remember {
        baseDatos.faenaDAO()
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
                } catch (_: Exception) {
                    // Se conserva el resto de las faenas si una consulta falla.
                }
            }
        }

        faenasPorUsuario = resultado
    }

    val asistenciaDAO = remember {
        baseDatos.asistenciaDAO()
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

                4 -> SeccionSupervisor(
                    titulo = "Notificaciones",
                    descripcion = "Aquí podrás consultar avisos y novedades relacionados con tu equipo."
                )
            }
        }
    )
}

// ---------------------------------------------------------
// INICIO DEL SUPERVISOR
// ---------------------------------------------------------

@Composable
fun ContenidoInicioSupervisor(
    onCambiarSeccion: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "GESTIÓN DE EQUIPO",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Centro de supervisión",
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Organiza y supervisa las actividades de tu equipo.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BosqueProfundo,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "PANEL DE SUPERVISIÓN",
                    color = VerdeVolt,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = "Tu equipo,\nbajo control.",
                    color = BlancoSupervisor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "Accede a la información de tus trabajadores y revisa el desarrollo de sus jornadas.",
                    color = Color(0xFFD0DDD5),
                    style = MaterialTheme.typography.bodyMedium
                )

                Surface(
                    color = VerdePrincipal,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "GESTIÓN OPERACIONAL",
                        color = VerdeVolt,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 8.dp
                        )
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tus herramientas",
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            val accesos = listOf(
                Triple(
                    "Personal a cargo",
                    "Consulta los trabajadores asignados a tu supervisión.",
                    1
                ),
                Triple(
                    "Asistencia del equipo",
                    "Revisa las entradas y salidas registradas.",
                    2
                ),
                Triple(
                    "Reportes de actividad",
                    "Consulta los indicadores de las jornadas.",
                    3
                )
            )

            accesos.forEachIndexed { indice, acceso ->
                TarjetaAccesoSupervisor(
                    titulo = acceso.first,
                    descripcion = acceso.second,
                    numero = "0${indice + 1}",
                    onClick = {
                        onCambiarSeccion(acceso.third)
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------
// TARJETAS DE ACCESO
// ---------------------------------------------------------

@Composable
fun TarjetaAccesoSupervisor(
    titulo: String,
    descripcion: String,
    numero: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoSupervisor
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = SuperficieSuave,
                shape = RoundedCornerShape(15.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = numero,
                        color = VerdePrincipal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titulo,
                    color = BosqueProfundo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = descripcion,
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "→",
                color = VerdePrincipal,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

// ---------------------------------------------------------
// SECCIÓN DE NOTIFICACIONES / PLACEHOLDER
// ---------------------------------------------------------

@Composable
fun SeccionSupervisor(
    titulo: String,
    descripcion: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "SUPERVISIÓN",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = titulo,
                color = BosqueProfundo,
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
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = BlancoSupervisor
            ),
            border = BorderStroke(1.dp, Borde),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = SuperficieSuave,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "PRÓXIMAMENTE",
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 7.dp
                        ),
                        color = VerdePrincipal,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Estamos preparando este espacio.",
                    color = BosqueProfundo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Este módulo estará disponible próximamente para facilitar la coordinación del trabajo en faena.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ---------------------------------------------------------
// PERSONAL A CARGO
// ---------------------------------------------------------

@Composable
fun SeccionPersonalACargo(
    personal: List<Usuario>,
    cargando: Boolean,
    mensajeError: String?,
    faenasPorUsuario: Map<String, Faena>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "EQUIPO DE TRABAJO",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Personal a cargo",
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Trabajadores y operarios asignados a tu supervisión.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        when {
            cargando -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterHorizontally),
                    color = VerdePrincipal,
                    strokeWidth = 3.dp
                )
            }

            mensajeError != null -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFEEEE),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = mensajeError,
                        color = EstadoError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            personal.isEmpty() -> {
                TarjetaEstadoVacioSupervisor(
                    titulo = "Sin trabajadores asignados",
                    descripcion = "Cuando se asignen operarios a tu equipo, aparecerán listados en esta sección."
                )
            }

            else -> {
                Surface(
                    color = BosqueProfundo,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "${personal.size} trabajadores asignados",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeVolt,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 9.dp
                        )
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    personal.forEach { trabajador ->
                        TarjetaPersonalSupervisor(
                            trabajador = trabajador,
                            faena = faenasPorUsuario[trabajador.id]
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPersonalSupervisor(
    trabajador: Usuario,
    faena: Faena?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoSupervisor
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = trabajador.nombreCompleto,
                        color = BosqueProfundo,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = trabajador.cargo,
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                EtiquetaEstadoSupervisor(
                    texto = if (trabajador.activo) "ACTIVO" else "INACTIVO",
                    activo = trabajador.activo
                )
            }

            HorizontalDivider(color = Borde)

            Text(
                text = "IDENTIFICACIÓN",
                color = TextoSecundario,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = trabajador.id,
                color = TextoPrincipal,
                style = MaterialTheme.typography.bodyMedium
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Fondo,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(13.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "FAENA ASIGNADA",
                        color = TextoSecundario,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = when {
                            trabajador.faenaId == null -> "Sin faena asignada"
                            faena != null -> faena.nombre
                            else -> "Faena no disponible"
                        },
                        color = BosqueProfundo,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )

                    if (faena != null) {
                        Text(
                            text = "Ubicación: ${faena.ubicacion}",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = if (faena.activa) {
                                "Faena activa"
                            } else {
                                "Faena finalizada"
                            },
                            color = if (faena.activa) {
                                EstadoExitoso
                            } else {
                                EstadoError
                            },
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------
// ASISTENCIA DEL EQUIPO
// ---------------------------------------------------------

@Composable
fun SeccionAsistenciaEquipo(
    personal: List<Usuario>,
    asistencias: List<Asistencia>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "CONTROL DE JORNADA",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Asistencia del equipo",
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Consulta las entradas y salidas registradas por tus trabajadores.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (asistencias.isEmpty()) {
            TarjetaEstadoVacioSupervisor(
                titulo = "Sin marcaciones registradas",
                descripcion = "Todavía no existen entradas o salidas registradas para tu equipo."
            )
        } else {
            Surface(
                color = BosqueProfundo,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "${asistencias.size} marcaciones registradas",
                    color = VerdeVolt,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 9.dp
                    )
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                            containerColor = BlancoSupervisor
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Borde),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = trabajador?.nombreCompleto
                                            ?: asistencia.usuarioId,
                                        color = BosqueProfundo,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = "ID: ${asistencia.usuarioId}",
                                        color = TextoSecundario,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                                EtiquetaEstadoSupervisor(
                                    texto = asistencia.tipo,
                                    activo = esEntrada
                                )
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
}

// ---------------------------------------------------------
// REPORTES DE ACTIVIDAD
// ---------------------------------------------------------

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

    val trabajadoresSinMarcaciones =
        (personal.size - ultimaMarcacionPorUsuario.size).coerceAtLeast(0)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "RESUMEN OPERACIONAL",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Reportes de actividad",
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Indicadores generales de las marcaciones de tu equipo.",
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
                containerColor = BlancoSupervisor
            ),
            border = BorderStroke(1.dp, Borde),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Estado actual del equipo",
                    color = BosqueProfundo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                FilaEstadoReporte(
                    etiqueta = "Con última marcación de entrada",
                    valor = trabajadoresEnJornada.toString(),
                    color = EstadoExitoso
                )

                FilaEstadoReporte(
                    etiqueta = "Con última marcación de salida",
                    valor = trabajadoresFueraDeJornada.toString(),
                    color = EstadoError
                )

                FilaEstadoReporte(
                    etiqueta = "Sin marcaciones",
                    valor = trabajadoresSinMarcaciones.toString(),
                    color = TextoSecundario
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SuperficieSuave,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Los totales de entradas y salidas corresponden a todas las marcaciones históricas disponibles, no solamente al día actual.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

// ---------------------------------------------------------
// COMPONENTES AUXILIARES DE REPORTES
// ---------------------------------------------------------

@Composable
private fun TarjetaResumenSupervisor(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = BlancoSupervisor
        ),
        border = BorderStroke(1.dp, Borde),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = valor,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun FilaEstadoReporte(
    etiqueta: String,
    valor: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            modifier = Modifier.weight(1f),
            color = TextoSecundario,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = valor,
            color = color,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun EtiquetaEstadoSupervisor(
    texto: String,
    activo: Boolean
) {
    Surface(
        color = if (activo) {
            Color(0xFFE8F4EC)
        } else {
            Color(0xFFFCEBEC)
        },
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            color = if (activo) EstadoExitoso else EstadoError,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun TarjetaEstadoVacioSupervisor(
    titulo: String,
    descripcion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoSupervisor
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = descripcion,
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
