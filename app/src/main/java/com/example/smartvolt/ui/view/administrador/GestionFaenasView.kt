package com.example.smartvolt.ui.view.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartvolt.modelo.Faena
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.theme.AzulOscuro
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.ui.theme.smartVoltTextFieldColors
import com.example.smartvolt.viewmodel.FaenaViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModel

private val FondoFaenas = Color(0xFFF4F6F2)
private val SuperficieFaenas = Color(0xFFE9EFE9)
private val TextoFaenas = Color(0xFF172522)
private val VerdeFaenas = Color(0xFF1E5147)
private val BlancoFaenas = Color(0xFFFFFFFF)
private val VerdeEstadoFaena = Color(0xFF2E7D5B)
private val FondoEstadoFaena = Color(0xFFEBFBEE)
private val GrisEstadoFaena = Color(0xFF718078)

@Composable
fun GestionFaenasView(
    faenaViewModel: FaenaViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estadoFaenas by faenaViewModel.uiState.collectAsStateWithLifecycle()
    val estadoUsuarios by usuarioViewModel.uiState.collectAsStateWithLifecycle()

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var faenaEditando by remember {
        mutableStateOf<Faena?>(null)
    }

    val operarios = estadoUsuarios.usuarios.filter {
        it.rol.equals("OPERARIO", ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // ENCABEZADO
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "ADMINISTRACIÓN",
                color = VerdeFaenas,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Gestión de faenas",
                color = TextoFaenas,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Organiza los centros de trabajo y administra al personal asignado.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // RESUMEN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResumenFaenas(
                titulo = "Registradas",
                cantidad = estadoFaenas.faenas.size.toString(),
                modifier = Modifier.weight(1f)
            )

            ResumenFaenas(
                titulo = "Activas",
                cantidad = estadoFaenas.faenas.count { it.activa }.toString(),
                modifier = Modifier.weight(1f)
            )

            ResumenFaenas(
                titulo = "Operarios",
                cantidad = operarios.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        // CREAR FAENA
        Button(
            onClick = {
                faenaEditando = null
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulOscuro,
                contentColor = VerdeVolt
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "+  Crear nueva faena",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 7.dp)
            )
        }

        // MENSAJES
        estadoFaenas.mensaje?.let { mensaje ->
            MensajeFaena(
                mensaje = mensaje,
                error = false
            )

            LaunchedEffect(mensaje) {
                faenaViewModel.limpiarMensajes()
            }
        }

        estadoFaenas.mensajeError?.let { error ->
            MensajeFaena(
                mensaje = error,
                error = true
            )
        }

        // ENCABEZADO DEL LISTADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Faenas registradas",
                color = TextoFaenas,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = SuperficieFaenas,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${estadoFaenas.faenas.size} faenas",
                    color = VerdeFaenas,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
                )
            }
        }

        // ESTADOS DE CARGA Y LISTADO
        when {
            estadoFaenas.cargando -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(34.dp),
                        color = VerdeFaenas,
                        strokeWidth = 3.dp
                    )

                    Text(
                        text = "Cargando faenas...",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            estadoFaenas.faenas.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BlancoFaenas
                    ),
                    border = BorderStroke(1.dp, Borde),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⌖",
                            color = VerdeFaenas,
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text(
                            text = "Todavía no hay faenas",
                            color = TextoFaenas,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Crea la primera faena para comenzar a organizar al personal.",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    estadoFaenas.faenas.forEach { faena ->
                        TarjetaFaena(
                            faena = faena,
                            operarios = operarios,
                            onEditar = {
                                faenaEditando = faena
                                mostrarFormulario = true
                            },
                            onCambiarEstado = {
                                faenaViewModel.cambiarEstado(faena)
                            },
                            onAsignarOperario = { usuario, asignar ->
                                usuarioViewModel.asignarFaena(
                                    usuarioId = usuario.id,
                                    faenaId = if (asignar) faena.id else null
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // FORMULARIO
    if (mostrarFormulario) {
        FormularioFaena(
            faena = faenaEditando,
            onCerrar = {
                mostrarFormulario = false
                faenaEditando = null
            },
            onGuardar = { faena ->
                faenaViewModel.guardarFaena(faena)
                mostrarFormulario = false
                faenaEditando = null
            }
        )
    }
}

// --------------------------------------------------
// RESUMEN
// --------------------------------------------------

@Composable
private fun ResumenFaenas(
    titulo: String,
    cantidad: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoFaenas
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = cantidad,
                color = VerdeFaenas,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = titulo,
                color = TextoSecundario,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// --------------------------------------------------
// MENSAJES
// --------------------------------------------------

@Composable
private fun MensajeFaena(
    mensaje: String,
    error: Boolean
) {
    val color = if (error) {
        MaterialTheme.colorScheme.error
    } else {
        VerdeEstadoFaena
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (error) Color(0xFFFFEEEE) else FondoEstadoFaena,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )

            Text(
                text = mensaje,
                color = color,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// --------------------------------------------------
// TARJETA DE FAENA
// --------------------------------------------------

@Composable
private fun TarjetaFaena(
    faena: Faena,
    operarios: List<Usuario>,
    onEditar: () -> Unit,
    onCambiarEstado: () -> Unit,
    onAsignarOperario: (Usuario, Boolean) -> Unit
) {
    var mostrarPersonal by remember(faena.id) {
        mutableStateOf(false)
    }

    val asignados = operarios.count {
        it.faenaId == faena.id
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoFaenas
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // NOMBRE Y ESTADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            SuperficieFaenas,
                            RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌖",
                        color = VerdeFaenas,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = faena.nombre,
                        color = TextoFaenas,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = faena.ubicacion,
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    color = if (faena.activa) {
                        FondoEstadoFaena
                    } else {
                        SuperficieFaenas
                    },
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = if (faena.activa) "ACTIVA" else "INACTIVA",
                        color = if (faena.activa) {
                            VerdeEstadoFaena
                        } else {
                            GrisEstadoFaena
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Text(
                text = faena.descripcion.ifBlank {
                    "Sin descripción registrada."
                },
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )

            // FECHAS
            Surface(
                color = FondoFaenas,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(11.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "PERÍODO DE TRABAJO",
                        color = VerdeFaenas,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "Inicio: ${faena.fechaInicio}",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Término: ${faena.fechaTermino}",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // PERSONAL ASIGNADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$asignados operarios asignados",
                    color = TextoFaenas,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )

                TextButton(
                    onClick = {
                        mostrarPersonal = !mostrarPersonal
                    }
                ) {
                    Text(
                        text = if (mostrarPersonal) {
                            "Ocultar personal"
                        } else {
                            "Ver personal"
                        },
                        color = VerdeFaenas,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = Borde)

            // ACCIONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Editar",
                        color = VerdeFaenas,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = onCambiarEstado,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (faena.activa) "Desactivar" else "Activar",
                        color = if (faena.activa) {
                            GrisEstadoFaena
                        } else {
                            VerdeEstadoFaena
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ASIGNACIÓN DE PERSONAL
            if (mostrarPersonal) {
                HorizontalDivider(color = Borde)

                Text(
                    text = "Personal de la faena",
                    color = TextoFaenas,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )

                if (operarios.isEmpty()) {
                    Text(
                        text = "No hay operarios registrados.",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    operarios.forEach { usuario ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = usuario.faenaId == faena.id,
                                onCheckedChange = { marcado ->
                                    onAsignarOperario(usuario, marcado)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = VerdeFaenas,
                                    uncheckedColor = TextoSecundario
                                )
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = usuario.nombreCompleto,
                                    color = TextoFaenas,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = usuario.id,
                                    color = TextoSecundario,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            if (usuario.faenaId == faena.id) {
                                Surface(
                                    color = FondoEstadoFaena,
                                    shape = RoundedCornerShape(7.dp)
                                ) {
                                    Text(
                                        text = "ASIGNADO",
                                        color = VerdeEstadoFaena,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            horizontal = 7.dp,
                                            vertical = 4.dp
                                        )
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

// --------------------------------------------------
// FORMULARIO DE FAENA
// --------------------------------------------------

@Composable
private fun FormularioFaena(
    faena: Faena?,
    onCerrar: () -> Unit,
    onGuardar: (Faena) -> Unit
) {
    var nombre by remember(faena) {
        mutableStateOf(faena?.nombre ?: "")
    }

    var ubicacion by remember(faena) {
        mutableStateOf(faena?.ubicacion ?: "")
    }

    var descripcion by remember(faena) {
        mutableStateOf(faena?.descripcion ?: "")
    }

    var fechaInicio by remember(faena) {
        mutableStateOf(faena?.fechaInicio ?: "")
    }

    var fechaTermino by remember(faena) {
        mutableStateOf(faena?.fechaTermino ?: "")
    }

    var latitudTexto by remember(faena) { mutableStateOf(faena?.latitud?.toString() ?: "") }
    var longitudTexto by remember(faena) { mutableStateOf(faena?.longitud?.toString() ?: "") }

    val coordenadasValidas = latitudTexto.toDoubleOrNull()?.let { it in -90.0..90.0 } == true &&
        longitudTexto.toDoubleOrNull()?.let { it in -180.0..180.0 } == true
    val formularioValido =
        nombre.isNotBlank() &&
                ubicacion.isNotBlank() &&
                fechaInicio.isNotBlank() &&
                fechaTermino.isNotBlank() && coordenadasValidas

    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(24.dp),
        containerColor = BlancoFaenas,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = if (faena == null) {
                        "Nueva faena"
                    } else {
                        "Editar faena"
                    },
                    color = TextoFaenas,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Ingresa la información del centro de trabajo.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(11.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de la faena") },
                    placeholder = { Text("Ej. Proyecto Solar Norte") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = smartVoltTextFieldColors()
                )

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ubicación") },
                    placeholder = { Text("Ej. Antofagasta") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = smartVoltTextFieldColors()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción") },
                    placeholder = { Text("Describe brevemente la faena") },
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 3,
                    colors = smartVoltTextFieldColors()
                )

                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fecha de inicio") },
                    placeholder = { Text("DD/MM/AAAA") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = smartVoltTextFieldColors()
                )

                OutlinedTextField(
                    value = fechaTermino,
                    onValueChange = { fechaTermino = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fecha de término") },
                    placeholder = { Text("DD/MM/AAAA") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = smartVoltTextFieldColors()
                )

                OutlinedTextField(
                    value = latitudTexto,
                    onValueChange = { latitudTexto = it },
                    label = { Text("Latitud GPS (ej. -33.45)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = smartVoltTextFieldColors()
                )
                OutlinedTextField(
                    value = longitudTexto,
                    onValueChange = { longitudTexto = it },
                    label = { Text("Longitud GPS (ej. -70.65)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = smartVoltTextFieldColors()
                )
                Text("Radio de control: 100 m · Bluetooth: simulación", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Los campos con información obligatoria deben completarse antes de guardar.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                enabled = formularioValido,
                onClick = {
                    onGuardar(
                        Faena(
                            id = faena?.id ?: 0,
                            nombre = nombre.trim(),
                            ubicacion = ubicacion.trim(),
                            descripcion = descripcion.trim(),
                            fechaInicio = fechaInicio.trim(),
                            fechaTermino = fechaTermino.trim(),
                            activa = faena?.activa ?: true,
                            latitud = latitudTexto.toDouble(),
                            longitud = longitudTexto.toDouble(),
                            beaconId = faena?.beaconId
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulOscuro,
                    contentColor = VerdeVolt
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Guardar faena",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text(
                    text = "Cancelar",
                    color = TextoSecundario
                )
            }
        }
    )
}
