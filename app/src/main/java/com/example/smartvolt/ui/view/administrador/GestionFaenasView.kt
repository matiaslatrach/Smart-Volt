package com.example.smartvolt.ui.view.administrador

import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.TextoPrincipal
import com.example.smartvolt.ui.theme.SuperficieSuave
import com.example.smartvolt.ui.theme.FondoExito
import com.example.smartvolt.ui.theme.Fondo
import com.example.smartvolt.ui.theme.EstadoExitoso
import com.example.smartvolt.ui.theme.Blanco
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.smartvolt.ui.components.CampoTextoSmartVolt
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.theme.AzulOscuro
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.viewmodel.FaenaViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModel

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
                color = VerdePrincipal,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Gestión de faenas",
                color = TextoPrincipal,
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
                color = TextoPrincipal,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Surface(
                color = SuperficieSuave,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${estadoFaenas.faenas.size} faenas",
                    color = VerdePrincipal,
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
                        color = VerdePrincipal,
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
                        containerColor = Blanco
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
                            color = VerdePrincipal,
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text(
                            text = "Todavía no hay faenas",
                            color = TextoPrincipal,
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
            containerColor = Blanco
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
                color = VerdePrincipal,
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
        EstadoExitoso
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (error) Color(0xFFFFEEEE) else FondoExito,
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
            containerColor = Blanco
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
                            SuperficieSuave,
                            RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌖",
                        color = VerdePrincipal,
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
                        color = TextoPrincipal,
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
                        FondoExito
                    } else {
                        SuperficieSuave
                    },
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Text(
                        text = if (faena.activa) "ACTIVA" else "INACTIVA",
                        color = if (faena.activa) {
                            EstadoExitoso
                        } else {
                            TextoSecundario
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
                color = Fondo,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(11.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "PERÍODO DE TRABAJO",
                        color = VerdePrincipal,
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
                    color = TextoPrincipal,
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
                        color = VerdePrincipal,
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
                        color = VerdePrincipal,
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
                            TextoSecundario
                        } else {
                            EstadoExitoso
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
                    color = TextoPrincipal,
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
                                    checkedColor = VerdePrincipal,
                                    uncheckedColor = TextoSecundario
                                )
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = usuario.nombreCompleto,
                                    color = TextoPrincipal,
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
                                    color = FondoExito,
                                    shape = RoundedCornerShape(7.dp)
                                ) {
                                    Text(
                                        text = "ASIGNADO",
                                        color = EstadoExitoso,
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

    val formularioValido =
        nombre.isNotBlank() &&
                ubicacion.isNotBlank() &&
                fechaInicio.isNotBlank() &&
                fechaTermino.isNotBlank()

    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(24.dp),
        containerColor = Blanco,
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
                    color = TextoPrincipal,
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
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                CampoTextoSmartVolt(
                    valor = nombre,
                    alCambiar = { nombre = it },
                    etiqueta = "Nombre de la faena",
                    ejemplo = "Ej. Proyecto Solar Norte"
                )

                CampoTextoSmartVolt(
                    valor = ubicacion,
                    alCambiar = { ubicacion = it },
                    etiqueta = "Ubicación",
                    ejemplo = "Ej. Antofagasta"
                )

                CampoTextoSmartVolt(
                    valor = descripcion,
                    alCambiar = { descripcion = it },
                    etiqueta = "Descripción",
                    ejemplo = "Describe brevemente la faena",
                    unaLinea = false,
                    lineasMinimas = 2,
                    lineasMaximas = 3
                )

                CampoTextoSmartVolt(
                    valor = fechaInicio,
                    alCambiar = { fechaInicio = it },
                    etiqueta = "Fecha de inicio",
                    ejemplo = "DD/MM/AAAA"
                )

                CampoTextoSmartVolt(
                    valor = fechaTermino,
                    alCambiar = { fechaTermino = it },
                    etiqueta = "Fecha de término",
                    ejemplo = "DD/MM/AAAA"
                )

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
                            activa = faena?.activa ?: true
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
