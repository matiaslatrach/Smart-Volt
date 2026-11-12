
package com.example.smartvolt.ui.view.operario

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
import androidx.compose.ui.unit.dp
import com.example.smartvolt.dominio.JornadaCalculator
import com.example.smartvolt.viewmodel.AsistenciaViewModel

// PALETA SMARTVOLT
private val BosqueProfundo = Color(0xFF142B2A)
private val VerdePrincipal = Color(0xFF1E5147)
private val VerdeVolt = Color(0xFFC7E56A)
private val FondoSuave = Color(0xFFF4F6F2)
private val TextoSecundario = Color(0xFF718078)
private val BordeSuave = Color(0xFFE9EFE9)
private val VerdeEstado = Color(0xFF397B55)
private val RojoEstado = Color(0xFFB64D4D)

@Composable
fun HistorialOperarioView(
    viewModel: AsistenciaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val jornadas = remember(uiState.historial) {
        JornadaCalculator.agrupar(uiState.historial)
    }

    val totalMs = jornadas
        .mapNotNull { it.duracionMs }
        .sum()

    val jornadasCompletas = jornadas.count {
        it.salida != null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoSuave)
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // CABECERA
        Column(
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = "Mi historial",
                style = MaterialTheme.typography.headlineSmall,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Consulta tus marcaciones y revisa el tiempo registrado en tus jornadas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        // RESUMEN GENERAL
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = VerdePrincipal,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "RESUMEN DE ACTIVIDAD",
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
                    text = JornadaCalculator.formatoDuracion(totalMs),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tiempo total registrado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD0DED5)
                )

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.16f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ResumenDato(
                        valor = jornadas.size.toString(),
                        etiqueta = "Jornadas"
                    )

                    ResumenDato(
                        valor = jornadasCompletas.toString(),
                        etiqueta = "Completas"
                    )

                    ResumenDato(
                        valor = (jornadas.size - jornadasCompletas).toString(),
                        etiqueta = "Pendientes"
                    )
                }
            }
        }

        // LISTADO DE JORNADAS
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tus jornadas",
                style = MaterialTheme.typography.titleLarge,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Duraciones calculadas entre entrada y salida.",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )

            if (jornadas.isEmpty()) {
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
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = FondoSuave,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "—",
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 10.dp
                                ),
                                color = VerdePrincipal,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Todavía no tienes jornadas registradas",
                            style = MaterialTheme.typography.titleMedium,
                            color = BosqueProfundo,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Cuando registres tu asistencia, tus jornadas aparecerán aquí.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                    }
                }
            }

            jornadas.forEach { jornada ->
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
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // FECHA Y ESTADO
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
                                    text = "JORNADA",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextoSecundario,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = JornadaCalculator.formatoFecha(
                                        jornada.entrada.fechaHora
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BosqueProfundo,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                color = if (jornada.salida != null) {
                                    Color(0xFFE7F3E9)
                                } else {
                                    Color(0xFFFFF3D9)
                                },
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = if (jornada.salida != null) {
                                        "COMPLETA"
                                    } else {
                                        "PENDIENTE"
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = 11.dp,
                                        vertical = 7.dp
                                    ),
                                    color = if (jornada.salida != null) {
                                        VerdeEstado
                                    } else {
                                        Color(0xFF94702A)
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = BordeSuave)

                        // ENTRADA
                        FilaMarcacion(
                            etiqueta = "Entrada",
                            hora = JornadaCalculator.formatoHora(
                                jornada.entrada.fechaHora
                            ),
                            color = VerdePrincipal
                        )

                        // SALIDA
                        FilaMarcacion(
                            etiqueta = "Salida",
                            hora = jornada.salida?.let {
                                JornadaCalculator.formatoHora(it.fechaHora)
                            } ?: "Pendiente",
                            color = if (jornada.salida != null) {
                                VerdePrincipal
                            } else {
                                TextoSecundario
                            }
                        )

                        // DURACIÓN
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = FondoSuave,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Duración de jornada",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = JornadaCalculator.formatoDuracion(
                                        jornada.duracionMs
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = VerdePrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // INCIDENCIAS
                        if (jornada.incidencia) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = RojoEstado.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Revisar marcaciones incompletas",
                                    modifier = Modifier.padding(12.dp),
                                    color = RojoEstado,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // MET. DE REGISTRO
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Origen del registro",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextoSecundario
                            )

                            Text(
                                text = jornada.entrada.metodoRegistro,
                                style = MaterialTheme.typography.bodySmall,
                                color = BosqueProfundo,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenDato(
    valor: String,
    etiqueta: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleLarge,
            color = VerdeVolt,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFD0DED5)
        )
    }
}

@Composable
private fun FilaMarcacion(
    etiqueta: String,
    hora: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(9.dp),
                shape = RoundedCornerShape(50.dp),
                color = color
            ) {}

            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        Text(
            text = hora,
            style = MaterialTheme.typography.bodyMedium,
            color = BosqueProfundo,
            fontWeight = FontWeight.SemiBold
        )
    }
}
