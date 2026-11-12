
package com.example.smartvolt.ui.view.supervisor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.SolicitudExcepcionRepositorio
import kotlinx.coroutines.launch

private val BosqueProfundo = Color(0xFF142B2A)
private val VerdePrincipal = Color(0xFF1E5147)
private val VerdeVolt = Color(0xFFC7E56A)
private val FondoSuave = Color(0xFFF4F6F2)
private val TextoSecundario = Color(0xFF718078)
private val BordeSuave = Color(0xFFE9EFE9)
private val VerdeEstado = Color(0xFF397B55)
private val RojoEstado = Color(0xFFB64D4D)
private val TextoClaro = Color(0xFFD0DED5)

@Composable
fun SolicitudesSupervisorView(supervisorId: String) {
    val context = LocalContext.current

    val repo = remember(context) {
        SolicitudExcepcionRepositorio(
            AppDataBase.obtenerBaseDatos(context).solicitudExcepcionDAO()
        )
    }

    val solicitudes by repo.pendientes(supervisorId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoSuave)
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {

        // ENCABEZADO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = BosqueProfundo
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Surface(
                    color = VerdePrincipal,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "GESTIÓN OPERATIVA",
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
                    text = "Solicitudes excepcionales",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Revisa los inconvenientes informados por los operarios y gestiona sus solicitudes de asistencia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoClaro
                )

                Surface(
                    color = VerdePrincipal,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${solicitudes.size} solicitudes pendientes",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        color = VerdeVolt,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // AVISO DEL SISTEMA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE9EFE9),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Demostración local: las solicitudes se gestionan en esta instalación y no se envían a otro teléfono.",
                modifier = Modifier.padding(14.dp),
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // MENSAJE DE OPERACIÓN
        mensaje?.let {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = VerdeEstado.copy(alpha = 0.09f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(14.dp),
                    color = VerdeEstado,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // LISTADO DE SOLICITUDES
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bandeja de solicitudes",
                style = MaterialTheme.typography.titleLarge,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold
            )

            if (solicitudes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, BordeSuave),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Todo al día",
                            style = MaterialTheme.typography.titleMedium,
                            color = BosqueProfundo,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "No hay solicitudes pendientes por revisar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                    }
                }
            }

            solicitudes.forEach { solicitud ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, BordeSuave),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {

                        // IDENTIFICACIÓN
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Operario",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextoSecundario
                                )

                                Text(
                                    text = solicitud.usuarioId,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BosqueProfundo,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = VerdeVolt.copy(alpha = 0.28f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = solicitud.tipo,
                                    modifier = Modifier.padding(
                                        horizontal = 11.dp,
                                        vertical = 7.dp
                                    ),
                                    color = VerdePrincipal,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = BordeSuave)

                        // MOTIVO
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Motivo de la solicitud",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextoSecundario
                            )

                            Text(
                                text = solicitud.motivo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = BosqueProfundo
                            )
                        }

                        // ESTADO DE VALIDACIONES
                        Column(
                            verticalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Text(
                                text = "Validaciones registradas",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextoSecundario
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EstadoSolicitud(
                                    titulo = "Bluetooth",
                                    detalle = if (solicitud.bluetoothValido) {
                                        "Válido"
                                    } else {
                                        "No detectado"
                                    },
                                    valido = solicitud.bluetoothValido,
                                    modifier = Modifier.weight(1f)
                                )

                                EstadoSolicitud(
                                    titulo = "GPS",
                                    detalle = if (solicitud.gpsValido) {
                                        "Válido"
                                    } else {
                                        "No válido"
                                    },
                                    valido = solicitud.gpsValido,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        HorizontalDivider(color = BordeSuave)

                        // ACCIONES
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                onClick = {
                                    scope.launch {
                                        mensaje = if (
                                            repo.resolver(
                                                solicitud.id,
                                                supervisorId,
                                                true,
                                                "Aprobado en demo"
                                            )
                                        ) {
                                            "Solicitud aprobada"
                                        } else {
                                            "No se puede aprobar: revisa la secuencia de marcaciones"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VerdePrincipal,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Aprobar",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            OutlinedButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                onClick = {
                                    scope.launch {
                                        mensaje = if (
                                            repo.resolver(
                                                solicitud.id,
                                                supervisorId,
                                                false,
                                                "Rechazado en demo"
                                            )
                                        ) {
                                            "Solicitud rechazada"
                                        } else {
                                            "No se pudo rechazar"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = RojoEstado
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    RojoEstado.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Rechazar",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoSolicitud(
    titulo: String,
    detalle: String,
    valido: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (valido) VerdeEstado else RojoEstado

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )

            Text(
                text = detalle,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
