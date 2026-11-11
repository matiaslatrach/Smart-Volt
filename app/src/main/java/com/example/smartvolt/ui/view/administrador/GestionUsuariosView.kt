
package com.example.smartvolt.ui.view.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.theme.AzulMedio
import com.example.smartvolt.ui.theme.AzulOscuro
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.viewmodel.UsuarioViewModel

@Composable
fun GestionUsuariosView(
    usuarioViewModel: UsuarioViewModel
) {
    val estado by usuarioViewModel.uiState.collectAsState()

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var usuarioEditando by remember {
        mutableStateOf<Usuario?>(null)
    }

    var usuarioPorEliminar by remember {
        mutableStateOf<Usuario?>(null)
    }

    // Cerrar formularios después de una operación exitosa.
    LaunchedEffect(estado.mensaje) {
        when (estado.mensaje) {
            "Usuario creado correctamente." -> {
                mostrarFormulario = false
            }

            "Usuario actualizado correctamente." -> {
                usuarioEditando = null
            }
        }
    }

    val supervisores = estado.usuarios.filter {
        it.rol == "SUPERVISOR" && it.activo
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ENCABEZADO
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "ADMINISTRACIÓN",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Gestión de usuarios",
                color = AzulOscuro,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Administra las cuentas, cargos y estado de acceso del personal.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // BOTÓN CREAR USUARIO
        Button(
            onClick = {
                usuarioViewModel.limpiarMensajes()
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulOscuro,
                contentColor = VerdeVolt
            )
        ) {
            Text(
                text = "+ Crear usuario",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // MENSAJE DE ÉXITO
        estado.mensaje?.let { mensaje ->
            Surface(
                color = Color(0xFFEBFBEE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mensaje,
                    color = Color(0xFF27834A),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // MENSAJE DE ERROR
        estado.mensajeError?.let { error ->
            Surface(
                color = Color(0xFFFFEEEE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = Color(0xFFBA3030),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // LISTADO DE USUARIOS
        when {
            estado.cargando && estado.usuarios.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterHorizontally),
                    color = AzulOscuro,
                    strokeWidth = 3.dp
                )
            }

            estado.usuarios.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Borde)
                ) {
                    Text(
                        text = "No hay usuarios registrados en el sistema.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }

            else -> {
                Surface(
                    color = AzulMedio,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Usuarios registrados: ${estado.usuarios.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeVolt,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    estado.usuarios.forEach { usuario ->
                        TarjetaUsuario(
                            usuario = usuario,
                            actualizando = estado.usuarioActualizandoId == usuario.id,
                            onCambiarEstado = { activo ->
                                usuarioViewModel.actualizarEstadoUsuario(
                                    id = usuario.id,
                                    activo = activo
                                )
                            },
                            onEditar = {
                                usuarioViewModel.limpiarMensajes()
                                usuarioEditando = usuario
                            },
                            onEliminar = {
                                usuarioViewModel.limpiarMensajes()
                                usuarioPorEliminar = usuario
                            }
                        )
                    }
                }
            }
        }
    }

    // FORMULARIO DE CREACIÓN
    if (mostrarFormulario) {
        FormularioCrearUsuario(
            supervisores = supervisores,
            creando = estado.creandoUsuario,
            mensajeError = estado.mensajeError,
            onCerrar = {
                if (!estado.creandoUsuario) {
                    mostrarFormulario = false
                    usuarioViewModel.limpiarMensajes()
                }
            },
            onCrear = { id, nombre, contrasena, cargo, rol, supervisorId ->
                usuarioViewModel.crearUsuario(
                    id = id,
                    nombreCompleto = nombre,
                    contrasena = contrasena,
                    cargo = cargo,
                    rol = rol,
                    supervisorId = supervisorId
                )
            }
        )
    }

    // FORMULARIO DE EDICIÓN
    usuarioEditando?.let { usuario ->
        FormularioEditarUsuario(
            usuario = usuario,
            supervisores = supervisores,
            mensajeError = estado.mensajeError,
            onCerrar = {
                usuarioEditando = null
                usuarioViewModel.limpiarMensajes()
            },
            onGuardar = { usuarioActualizado ->
                usuarioViewModel.actualizarUsuario(
                    usuarioActualizado
                )
            }
        )
    }

    // CONFIRMACIÓN DE ELIMINACIÓN
    usuarioPorEliminar?.let { usuario ->

        AlertDialog(
            onDismissRequest = {
                usuarioPorEliminar = null
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Eliminar usuario",
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar a ${usuario.nombreCompleto} (${usuario.id})? Esta acción no se puede deshacer.",
                    color = TextoSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        usuarioViewModel.eliminarUsuario(usuario)
                        usuarioPorEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBA3030),
                        contentColor = Color.White
                    )
                ) {
                    Text("Eliminar definitivamente")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        usuarioPorEliminar = null
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = TextoSecundario
                    )
                }
            }
        )
    }
}



// --------------------------------------------------
// FORMULARIO DE CREACIÓN
// --------------------------------------------------

@Composable
private fun FormularioCrearUsuario(
    supervisores: List<Usuario>,
    creando: Boolean,
    mensajeError: String?,
    onCerrar: () -> Unit,
    onCrear: (
        String,
        String,
        String,
        String,
        String,
        String?
    ) -> Unit
) {
    var id by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("OPERARIO") }
    var supervisorId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Nuevo usuario",
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Completa los datos de la nueva cuenta.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it.uppercase() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("ID de usuario") },
                    placeholder = { Text("Ej. OP-002") },
                    singleLine = true,
                    enabled = !creando
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    placeholder = { Text("Ej. Juan Pérez") },
                    singleLine = true,
                    enabled = !creando
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    placeholder = { Text("Mínimo 6 caracteres") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !creando
                )

                OutlinedTextField(
                    value = cargo,
                    onValueChange = { cargo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cargo") },
                    placeholder = { Text("Ej. Operador eléctrico") },
                    singleLine = true,
                    enabled = !creando
                )

                Text(
                    text = "ROL DEL USUARIO",
                    color = TextoSecundario,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )

                SelectorDesplegable(
                    valor = rol,
                    opciones = listOf(
                        "OPERARIO",
                        "SUPERVISOR",
                        "ADMINISTRADOR"
                    ),
                    habilitado = !creando,
                    alSeleccionar = {
                        rol = it
                        supervisorId = ""
                    }
                )

                if (rol == "OPERARIO") {
                    Text(
                        text = "SUPERVISOR ASIGNADO",
                        color = TextoSecundario,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium
                    )

                    SelectorDesplegable(
                        valor = supervisorId,
                        opciones = listOf("") + supervisores.map { it.id },
                        habilitado = !creando,
                        textoVacio = "Sin supervisor",
                        etiqueta = { seleccionado ->
                            if (seleccionado.isBlank()) {
                                "Sin supervisor"
                            } else {
                                val supervisor = supervisores.find {
                                    it.id == seleccionado
                                }

                                supervisor?.let {
                                    "${it.nombreCompleto} (${it.id})"
                                } ?: seleccionado
                            }
                        },
                        alSeleccionar = {
                            supervisorId = it
                        }
                    )

                    if (supervisores.isEmpty()) {
                        Text(
                            text = "No hay supervisores activos registrados.",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                mensajeError?.let { error ->
                    Surface(
                        color = Color(0xFFFFEEEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFBA3030),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCrear(
                        id,
                        nombre,
                        contrasena,
                        cargo,
                        rol,
                        supervisorId.takeIf {
                            rol == "OPERARIO" && it.isNotBlank()
                        }
                    )
                },
                enabled = !creando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulOscuro,
                    contentColor = VerdeVolt
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (creando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = VerdeVolt,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Crear usuario",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCerrar,
                enabled = !creando
            ) {
                Text(
                    text = "Cancelar",
                    color = TextoSecundario
                )
            }
        }
    )
}

// --------------------------------------------------
// SELECTOR DESPLEGABLE REUTILIZABLE
// --------------------------------------------------

@Composable
private fun SelectorDesplegable(
    valor: String,
    opciones: List<String>,
    habilitado: Boolean,
    alSeleccionar: (String) -> Unit,
    textoVacio: String = "Seleccionar",
    etiqueta: (String) -> String = { it }
) {
    var expandido by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { expandido = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (valor.isBlank()) {
                        textoVacio
                    } else {
                        etiqueta(valor)
                    },
                    color = AzulOscuro
                )

                Text(
                    text = "▼",
                    color = TextoSecundario
                )
            }
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (opcion.isBlank()) {
                                textoVacio
                            } else {
                                etiqueta(opcion)
                            }
                        )
                    },
                    onClick = {
                        alSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

// --------------------------------------------------
// TARJETA DE USUARIO
// --------------------------------------------------

@Composable
private fun TarjetaUsuario(
    usuario: Usuario,
    actualizando: Boolean,
    onCambiarEstado: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Borde)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = usuario.nombreCompleto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )

                    Surface(
                        color = if (usuario.activo) {
                            Color(0xFFEBFBEE)
                        } else {
                            Color(0xFFFFEEEE)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (usuario.activo) "ACTIVA" else "INACTIVA",
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (usuario.activo) {
                                Color(0xFF27834A)
                            } else {
                                Color(0xFFBA3030)
                            }
                        )
                    }
                }

                Text(
                    text = "ID: ${usuario.id} | Cargo: ${usuario.cargo} | Rol: ${usuario.rol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditar,
                        enabled = !actualizando,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Editar",
                            color = AzulOscuro,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onEliminar,
                        enabled = !actualizando,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBA3030))
                    ) {
                        Text(
                            text = "Eliminar",
                            color = Color(0xFFBA3030),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (actualizando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = AzulOscuro,
                    strokeWidth = 2.dp
                )
            } else {
                Switch(
                    checked = usuario.activo,
                    onCheckedChange = onCambiarEstado,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = VerdeVolt,
                        checkedTrackColor = AzulOscuro,
                        uncheckedThumbColor = TextoSecundario,
                        uncheckedTrackColor = Borde
                    )
                )
            }
        }
    }
}

// --------------------------------------------------
// FORMULARIO DE EDICIÓN
// --------------------------------------------------

@Composable
private fun FormularioEditarUsuario(
    usuario: Usuario,
    supervisores: List<Usuario>,
    mensajeError: String?,
    onCerrar: () -> Unit,
    onGuardar: (Usuario) -> Unit
) {
    var nombre by remember(usuario.id) {
        mutableStateOf(usuario.nombreCompleto)
    }

    var cargo by remember(usuario.id) {
        mutableStateOf(usuario.cargo)
    }

    var rol by remember(usuario.id) {
        mutableStateOf(usuario.rol)
    }

    var supervisorId by remember(usuario.id) {
        mutableStateOf(usuario.supervisorId ?: "")
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(18.dp),
        containerColor = Color.White,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Editar usuario",
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Modifica los datos de la cuenta.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = usuario.id,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("ID de usuario") },
                    enabled = false,
                    singleLine = true
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = cargo,
                    onValueChange = { cargo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cargo") },
                    singleLine = true
                )

                Text(
                    text = "ROL DEL USUARIO",
                    color = TextoSecundario,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )

                SelectorDesplegable(
                    valor = rol,
                    opciones = listOf(
                        "OPERARIO",
                        "SUPERVISOR",
                        "ADMINISTRADOR"
                    ),
                    habilitado = true,
                    alSeleccionar = {
                        rol = it
                        supervisorId = ""
                    }
                )

                if (rol == "OPERARIO") {
                    Text(
                        text = "SUPERVISOR ASIGNADO",
                        color = TextoSecundario,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium
                    )

                    val supervisoresDisponibles = supervisores.filter {
                        it.id != usuario.id
                    }

                    SelectorDesplegable(
                        valor = supervisorId,
                        opciones = listOf("") + supervisoresDisponibles.map { it.id },
                        habilitado = true,
                        textoVacio = "Sin supervisor",
                        etiqueta = { seleccionado ->
                            if (seleccionado.isBlank()) {
                                "Sin supervisor"
                            } else {
                                val supervisor = supervisoresDisponibles.find {
                                    it.id == seleccionado
                                }

                                supervisor?.let {
                                    "${it.nombreCompleto} (${it.id})"
                                } ?: seleccionado
                            }
                        },
                        alSeleccionar = {
                            supervisorId = it
                        }
                    )

                    if (supervisoresDisponibles.isEmpty()) {
                        Text(
                            text = "No hay otros supervisores activos registrados.",
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                mensajeError?.let { error ->
                    Surface(
                        color = Color(0xFFFFEEEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFBA3030),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        usuario.copy(
                            nombreCompleto = nombre,
                            cargo = cargo,
                            rol = rol,
                            supervisorId = if (rol == "OPERARIO") {
                                supervisorId.takeIf { it.isNotBlank() }
                            } else {
                                null
                            }
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulOscuro,
                    contentColor = VerdeVolt
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Guardar cambios",
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
