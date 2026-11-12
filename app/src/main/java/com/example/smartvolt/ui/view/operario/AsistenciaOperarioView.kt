
package com.example.smartvolt.ui.view.operario

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.SolicitudExcepcionRepositorio
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.modelo.SolicitudExcepcion
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.servicios.*
import com.example.smartvolt.ui.theme.smartVoltTextFieldColors
import com.example.smartvolt.viewmodel.AsistenciaViewModel
import kotlinx.coroutines.launch

// PALETA DE COLORES SMARTVOLT
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
fun AsistenciaOperarioView(
    viewModel: AsistenciaViewModel,
    usuario: Usuario,
    faena: Faena?
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val beacon = remember { SimulatedBeaconValidator(false) }
    val gps = remember(context) { UbicacionValidator(context) }

    val solicitudes = remember(context) {
        SolicitudExcepcionRepositorio(
            AppDataBase.obtenerBaseDatos(context).solicitudExcepcionDAO()
        )
    }

    var beaconPresente by remember { mutableStateOf(false) }
    var validando by remember { mutableStateOf(false) }
    var resultado by remember { mutableStateOf<ResultadoValidacion?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var motivo by remember { mutableStateOf("") }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] != true) {
            resultado = ResultadoValidacion(
                beaconPresente,
                ResultadoGps(
                    false,
                    mensaje = "Permiso de ubicación precisa denegado"
                )
            )
            mensaje = "Puedes solicitar una excepción."
        } else {
            resultado = null
        }
    }

    val siguiente =
        if (estado.ultimaAsistencia?.tipo == "ENTRADA") "SALIDA" else "ENTRADA"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoSuave)
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // CABECERA
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
                    text = "Control de asistencia",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Verifica tu ubicación y los requisitos de acceso antes de registrar tu jornada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoClaro
                )

                Surface(
                    color = VerdePrincipal,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "RADIO DE VALIDACIÓN · 100 M",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        color = VerdeVolt,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // INFORMACIÓN DE LA FAENA
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
                border = BorderStroke(1.dp, BordeSuave),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    if (faena == null) {
                        Text(
                            text = "Sin faena asignada",
                            color = RojoEstado,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Contacta con tu supervisor para recibir una asignación.",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = faena.nombre,
                            color = VerdePrincipal,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            color = FondoSuave,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (
                                    faena.latitud != null &&
                                    faena.longitud != null
                                ) {
                                    "Ubicación configurada para validación GPS"
                                } else {
                                    "La ubicación GPS todavía no está configurada."
                                },
                                modifier = Modifier.padding(12.dp),
                                color = TextoSecundario,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (faena.latitud == null || faena.longitud == null) {
                            Text(
                                text = "Solicita al administrador configurar las coordenadas de esta faena.",
                                color = RojoEstado,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // VALIDACIÓN DE REQUISITOS
        Column(
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = "Validación de acceso",
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
                border = BorderStroke(1.dp, BordeSuave),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // BLUETOOTH
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Baliza Bluetooth",
                                color = BosqueProfundo,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Simulación de detección para pruebas",
                                color = TextoSecundario,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = beaconPresente,
                            onCheckedChange = {
                                beaconPresente = it
                                resultado = null
                                mensaje = null
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BosqueProfundo,
                                checkedTrackColor = VerdeVolt,
                                uncheckedThumbColor = TextoSecundario,
                                uncheckedTrackColor = BordeSuave
                            )
                        )
                    }

                    HorizontalDivider(color = BordeSuave)

                    // GPS
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Ubicación GPS",
                            color = BosqueProfundo,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Se comprobará tu posición actual respecto a la ubicación de la faena.",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = faena != null &&
                                faena.latitud != null &&
                                faena.longitud != null &&
                                !validando,
                        onClick = {
                            if (
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                pedirPermiso.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                val actual = faena ?: return@Button
                                validando = true

                                scope.launch {
                                    try {
                                        beacon.detectado = beaconPresente

                                        resultado = ValidadorAsistencia(
                                            beacon,
                                            gps
                                        ).validar(
                                            actual.id,
                                            actual.latitud,
                                            actual.longitud
                                        )

                                        mensaje = null
                                    } catch (e: Exception) {
                                        mensaje = "No fue posible validar el dispositivo."
                                    } finally {
                                        validando = false
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePrincipal,
                            contentColor = Color.White,
                            disabledContainerColor = BordeSuave,
                            disabledContentColor = TextoSecundario
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (validando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(19.dp),
                                color = VerdeVolt,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                        }

                        Text(
                            text = if (validando) {
                                "Comprobando requisitos..."
                            } else {
                                "Comprobar requisitos"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // RESULTADOS DE VALIDACIÓN
        resultado?.let { validacion ->
            Column(
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Text(
                    text = "Resultado de validación",
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
                    border = BorderStroke(1.dp, BordeSuave),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        EstadoValidacion(
                            titulo = "Bluetooth · DEMO",
                            detalle = if (validacion.bluetoothValido) {
                                "Baliza simulada detectada"
                            } else {
                                "No se detectó la baliza simulada"
                            },
                            valido = validacion.bluetoothValido
                        )

                        HorizontalDivider(color = BordeSuave)

                        EstadoValidacion(
                            titulo = "Ubicación GPS",
                            detalle = validacion.gps.mensaje,
                            valido = validacion.gps.valido
                        )

                        validacion.gps.distanciaMetros?.let { distancia ->
                            Text(
                                text = "Distancia aproximada: ${distancia.toInt()} m",
                                color = TextoSecundario,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (validacion.permitido) {
                            Surface(
                                color = VerdeVolt.copy(alpha = 0.24f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Requisitos cumplidos. Puedes registrar tu marcación.",
                                    modifier = Modifier.padding(13.dp),
                                    color = BosqueProfundo,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // MENSAJES DEL VIEWMODEL
        estado.mensaje?.let {
            MensajeAsistencia(it, valido = true)
        }

        estado.mensajeError?.let {
            MensajeAsistencia(it, valido = false)
        }

        mensaje?.let {
            MensajeAsistencia(it, valido = false)
        }

        // REGISTRO DE ASISTENCIA
        Column(
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = "Registro de jornada",
                style = MaterialTheme.typography.titleLarge,
                color = BosqueProfundo,
                fontWeight = FontWeight.Bold
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = resultado?.permitido == true &&
                        faena != null &&
                        !validando &&
                        !estado.cargando,
                onClick = {
                    val actual = faena ?: return@Button

                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        pedirPermiso.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else {
                        validando = true

                        scope.launch {
                            try {
                                beacon.detectado = beaconPresente

                                val nuevo = ValidadorAsistencia(
                                    beacon,
                                    gps
                                ).validar(
                                    actual.id,
                                    actual.latitud,
                                    actual.longitud
                                )

                                resultado = nuevo

                                if (nuevo.permitido) {
                                    viewModel.registrarMarcacion(
                                        nuevo,
                                        actual.id
                                    )
                                } else {
                                    mensaje = "La validación no fue satisfactoria. Puedes solicitar una excepción."
                                }
                            } catch (e: Exception) {
                                mensaje = "Ocurrió un error durante la validación."
                            } finally {
                                validando = false
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeVolt,
                    contentColor = BosqueProfundo,
                    disabledContainerColor = BordeSuave,
                    disabledContentColor = TextoSecundario
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (siguiente == "ENTRADA") {
                        "Registrar entrada"
                    } else {
                        "Registrar salida"
                    },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Text(
                text = "El registro se habilita una vez completadas las validaciones.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // SOLICITUD DE EXCEPCIÓN
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
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Text(
                    text = "¿Tienes un inconveniente?",
                    color = BosqueProfundo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Si no puedes completar la validación, puedes enviar una solicitud a tu supervisor.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo de la solicitud") },
                    placeholder = {
                        Text("Describe brevemente el inconveniente")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = smartVoltTextFieldColors(),
                    shape = RoundedCornerShape(14.dp)
                )


                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = faena != null &&
                            usuario.supervisorId != null &&
                            motivo.isNotBlank() &&
                            !validando,
                    onClick = {
                        val actual = faena ?: return@OutlinedButton
                        val supervisor = usuario.supervisorId
                            ?: return@OutlinedButton

                        scope.launch {
                            try {
                                val r = resultado

                                solicitudes.solicitar(
                                    SolicitudExcepcion(
                                        usuarioId = usuario.id,
                                        supervisorId = supervisor,
                                        faenaId = actual.id,
                                        tipo = siguiente,
                                        fechaSolicitud = System.currentTimeMillis(),
                                        motivo = motivo.trim(),
                                        bluetoothValido = r?.bluetoothValido == true,
                                        gpsValido = r?.gps?.valido == true
                                    )
                                )

                                mensaje = "Solicitud pendiente de aprobación."
                                motivo = ""
                            } catch (e: Exception) {
                                mensaje = "No fue posible crear la solicitud."
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = VerdePrincipal
                    ),
                    border = BorderStroke(1.dp, BordeSuave),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Solicitar excepción",
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}

@Composable
private fun EstadoValidacion(
    titulo: String,
    detalle: String,
    valido: Boolean
) {
    val color = if (valido) VerdeEstado else RojoEstado

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(10.dp),
            shape = RoundedCornerShape(50),
            color = color
        ) {}

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                color = BosqueProfundo,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = detalle,
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = if (valido) "OK" else "FALLÓ",
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MensajeAsistencia(
    texto: String,
    valido: Boolean
) {
    val color = if (valido) VerdeEstado else RojoEstado

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.09f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(14.dp),
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}
