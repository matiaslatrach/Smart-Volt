package com.example.smartvolt.ui.view.operario

import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.SuperficieSuave
import com.example.smartvolt.ui.theme.Fondo
import com.example.smartvolt.ui.theme.BosqueProfundo
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
                    viewModel = asistenciaViewModel,
                    faena = faenaAsignada
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
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {

        // Bienvenida
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BosqueProfundo,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = VerdePrincipal,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "ÁREA OPERATIVA",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        color = VerdeVolt,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Tu jornada comienza aquí.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Consulta tu faena, controla tu asistencia y mantente al día con tu jornada laboral.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD0DED5)
                )
            }
        }

        // Información de la faena
        Column(
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = "Mi faena asignada",
                style = MaterialTheme.typography.titleLarge,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    when {
                        cargandoFaena -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = VerdePrincipal,
                                strokeWidth = 3.dp
                            )

                            Text(
                                text = "Consultando tu faena...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        errorFaena -> {
                            Text(
                                text = "No fue posible consultar tu faena.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Intenta nuevamente más tarde o contacta a tu supervisor.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        faena == null -> {
                            Text(
                                text = "Sin faena asignada",
                                style = MaterialTheme.typography.titleMedium,
                                color = BosqueProfundo,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Actualmente no tienes una faena asignada. Contacta a tu supervisor o administrador.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FAENA ASIGNADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextoSecundario,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = if (faena.activa) {
                                        Color(0xFFE7F3E9)
                                    } else {
                                        Color(0xFFFCEAEA)
                                    }
                                ) {
                                    Text(
                                        text = if (faena.activa) {
                                            "ACTIVA"
                                        } else {
                                            "FINALIZADA"
                                        },
                                        modifier = Modifier.padding(
                                            horizontal = 11.dp,
                                            vertical = 6.dp
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (faena.activa) {
                                            Color(0xFF2E7D5B)
                                        } else {
                                            Color(0xFFC64B4B)
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = faena.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                color = VerdePrincipal,
                                fontWeight = FontWeight.Bold
                            )

                            FaenaDetalle(
                                etiqueta = "Ubicación",
                                valor = faena.ubicacion
                            )

                            if (faena.descripcion.isNotBlank()) {
                                FaenaDetalle(
                                    etiqueta = "Descripción",
                                    valor = faena.descripcion
                                )
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Fondo,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(9.dp)
                                ) {
                                    FaenaDetalle(
                                        etiqueta = "Fecha de inicio",
                                        valor = faena.fechaInicio.toString()
                                    )

                                    FaenaDetalle(
                                        etiqueta = "Fecha de término",
                                        valor = faena.fechaTermino.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Accesos rápidos
        Column(
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = "Accesos rápidos",
                style = MaterialTheme.typography.titleLarge,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Accede directamente a las herramientas de tu jornada.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )

            val accesos = listOf(
                Triple("Control de asistencia", "Registra tu entrada y salida", 1),
                Triple("Historial de jornadas", "Consulta tus registros anteriores", 2),
                Triple("Mi perfil", "Revisa tu información personal", 3)
            )

            accesos.forEach { (titulo, descripcion, indice) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCambiarSeccion(indice)
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(17.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SuperficieSuave,
                            shape = RoundedCornerShape(13.dp)
                        ) {
                            Text(
                                text = when (indice) {
                                    1 -> "01"
                                    2 -> "02"
                                    else -> "03"
                                },
                                modifier = Modifier.padding(12.dp),
                                color = VerdePrincipal,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = titulo,
                                style = MaterialTheme.typography.titleMedium,
                                color = BosqueProfundo,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }

                        Text(
                            text = "›",
                            style = MaterialTheme.typography.headlineMedium,
                            color = VerdePrincipal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaenaDetalle(
    etiqueta: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = BosqueProfundo,
            fontWeight = FontWeight.Medium
        )
    }
}
