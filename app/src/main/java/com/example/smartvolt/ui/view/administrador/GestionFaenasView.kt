package com.example.smartvolt.ui.view.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.smartvolt.viewmodel.FaenaViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModel

@Composable
fun GestionFaenasView(
    faenaViewModel: FaenaViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estadoFaenas by faenaViewModel.uiState.collectAsStateWithLifecycle()
    val estadoUsuarios by usuarioViewModel.uiState.collectAsStateWithLifecycle()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var faenaEditando by remember { mutableStateOf<Faena?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "ADMINISTRACIÓN",
                color = TextoSecundario,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Gestión de faenas",
                color = AzulOscuro,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Crea faenas y organiza al personal asignado.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

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
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("＋  Crear nueva faena")
        }

        estadoFaenas.mensaje?.let {
            Text(it, color = Color(0xFF267447))
            LaunchedEffect(it) {
                faenaViewModel.limpiarMensajes()
            }
        }

        estadoFaenas.mensajeError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (estadoFaenas.cargando) {
            CircularProgressIndicator()
        } else if (estadoFaenas.faenas.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Borde)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Todavía no hay faenas registradas.",
                        color = AzulOscuro,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Crea la primera faena para comenzar a organizar al personal.",
                        color = TextoSecundario
                    )
                }
            }
        } else {
            // Importante: usamos Column en lugar de LazyColumn.
            // PanelPrincipalView ya administra el desplazamiento vertical.
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                estadoFaenas.faenas.forEach { faena ->
                    TarjetaFaena(
                        faena = faena,
                        operarios = estadoUsuarios.usuarios.filter {
                            it.rol.equals("OPERARIO", ignoreCase = true)
                        },
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

    if (mostrarFormulario) {
        FormularioFaena(
            faena = faenaEditando,
            onCerrar = {
                mostrarFormulario = false
            },
            onGuardar = { faena ->
                faenaViewModel.guardarFaena(faena)
                mostrarFormulario = false
            }
        )
    }
}

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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Borde)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        faena.nombre,
                        color = AzulOscuro,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        faena.ubicacion,
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    color = if (faena.activa) Color(0xFFEAF5E8)
                    else Color(0xFFF0F2F5),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (faena.activa) "ACTIVA" else "INACTIVA",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = if (faena.activa) Color(0xFF267447)
                        else TextoSecundario,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                faena.descripcion,
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Inicio: ${faena.fechaInicio}  •  Término: ${faena.fechaTermino}",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onEditar) {
                    Text("Editar")
                }

                TextButton(onClick = onCambiarEstado) {
                    Text(
                        if (faena.activa) "Desactivar" else "Activar"
                    )
                }

                TextButton(
                    onClick = {
                        mostrarPersonal = !mostrarPersonal
                    }
                ) {
                    Text(
                        if (mostrarPersonal) "Ocultar personal"
                        else "Asignar personal"
                    )
                }
            }

            if (mostrarPersonal) {
                androidx.compose.material3.HorizontalDivider(
                    color = Borde
                )

                Text(
                    "Operarios asignados",
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold
                )

                if (operarios.isEmpty()) {
                    Text(
                        "No hay operarios registrados.",
                        color = TextoSecundario
                    )
                } else {
                    operarios.forEach { usuario ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = usuario.faenaId == faena.id,
                                onCheckedChange = { marcado ->
                                    onAsignarOperario(usuario, marcado)
                                }
                            )

                            Column {
                                Text(
                                    usuario.nombreCompleto,
                                    color = AzulOscuro
                                )

                                Text(
                                    usuario.id,
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
}

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
        title = {
            Text(
                if (faena == null) "Nueva faena" else "Editar faena",
                color = AzulOscuro,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la faena") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )

                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha de inicio (DD/MM/AAAA)") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = fechaTermino,
                    onValueChange = { fechaTermino = it },
                    label = { Text("Fecha de término (DD/MM/AAAA)") },
                    singleLine = true
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
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cancelar")
            }
        }
    )
}
