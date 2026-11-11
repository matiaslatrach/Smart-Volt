
package com.example.smartvolt.ui.view.administrador

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.smartvolt.ui.theme.smartVoltTextFieldColors
import com.example.smartvolt.viewmodel.UsuarioViewModel

private val FondoUsuarios = Color(0xFFF4F6F2)
private val SuperficieUsuarios = Color(0xFFE9EFE9)
private val TextoUsuarios = Color(0xFF172522)
private val VerdeUsuarios = Color(0xFF1E5147)
private val BlancoUsuarios = Color(0xFFFFFFFF)
private val VerdeExitoUsuarios = Color(0xFF2E7D5B)
private val RojoErrorUsuarios = Color(0xFFC64B4B)
private val FondoErrorUsuarios = Color(0xFFFFEEEE)
private val FondoExitoUsuarios = Color(0xFFEBFBEE)

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
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoUsuarios),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // ENCABEZADO
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "ADMINISTRACIÓN",
                color = VerdeUsuarios,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Gestión de usuarios",
                color = TextoUsuarios,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Administra las cuentas, cargos y permisos de acceso del personal.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // RESUMEN DE USUARIOS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ResumenUsuarios(
                titulo = "Registrados",
                cantidad = estado.usuarios.size.toString(),
                modifier = Modifier.weight(1f)
            )

            ResumenUsuarios(
                titulo = "Activos",
                cantidad = estado.usuarios.count { it.activo }.toString(),
                modifier = Modifier.weight(1f)
            )

            ResumenUsuarios(
                titulo = "Supervisores",
                cantidad = supervisores.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        // BOTÓN CREAR
        Button(
            onClick = {
                usuarioViewModel.limpiarMensajes()
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulOscuro,
                contentColor = VerdeVolt
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "+  Crear nuevo usuario",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 7.dp)
            )
        }

        // MENSAJES
        estado.mensaje?.let { mensaje ->
            MensajeUsuarios(
                mensaje = mensaje,
                esError = false
            )
        }

        estado.mensajeError?.let { error ->
            MensajeUsuarios(
                mensaje = error,
                esError = true
            )
        }

        // TÍTULO DEL LISTADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Personal registrado",
                color = TextoUsuarios,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Surface(
                color = SuperficieUsuarios,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${estado.usuarios.size} cuentas",
                    color = VerdeUsuarios,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
                )
            }
        }

        // LISTADO
        when {
            estado.cargando && estado.usuarios.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(34.dp),
                        color = VerdeUsuarios,
                        strokeWidth = 3.dp
                    )

                    Text(
                        text = "Cargando usuarios...",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            estado.usuarios.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BlancoUsuarios
                    ),
                    border = BorderStroke(1.dp, Borde)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "◎",
                            color = VerdeUsuarios,
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text(
                            text = "Aún no hay usuarios",
                            color = TextoUsuarios,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Crea la primera cuenta para comenzar a gestionar el personal.",
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
                usuarioViewModel.actualizarUsuario(usuarioActualizado)
            }
        )
    }

    // CONFIRMACIÓN DE ELIMINACIÓN
    usuarioPorEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = {
                usuarioPorEliminar = null
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = BlancoUsuarios,
            title = {
                Text(
                    text = "Eliminar usuario",
                    color = TextoUsuarios,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar a ${usuario.nombreCompleto} (${usuario.id})? Esta acción no se puede deshacer.",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        usuarioViewModel.eliminarUsuario(usuario)
                        usuarioPorEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoErrorUsuarios,
                        contentColor = BlancoUsuarios
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Eliminar definitivamente",
                        fontWeight = FontWeight.Bold
                    )
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
// RESUMEN DE USUARIOS
// --------------------------------------------------

@Composable
private fun ResumenUsuarios(
    titulo: String,
    cantidad: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoUsuarios
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = cantidad,
                color = VerdeUsuarios,
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
private fun MensajeUsuarios(
    mensaje: String,
    esError: Boolean
) {
    Surface(
        color = if (esError) FondoErrorUsuarios else FondoExitoUsuarios,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (esError) RojoErrorUsuarios else VerdeExitoUsuarios,
                        CircleShape
                    )
            )

            Text(
                text = mensaje,
                color = if (esError) RojoErrorUsuarios else VerdeExitoUsuarios,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
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
        shape = RoundedCornerShape(24.dp),
        containerColor = BlancoUsuarios,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Nuevo usuario",
                    color = TextoUsuarios,
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
                CampoUsuario(
                    valor = id,
                    alCambiar = { id = it.uppercase() },
                    etiqueta = "ID de usuario",
                    ejemplo = "Ej. OP-002",
                    habilitado = !creando
                )

                CampoUsuario(
                    valor = nombre,
                    alCambiar = { nombre = it },
                    etiqueta = "Nombre completo",
                    ejemplo = "Ej. Juan Pérez",
                    habilitado = !creando
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    placeholder = { Text("Mínimo 6 caracteres") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !creando,
                    shape = RoundedCornerShape(14.dp),
                    colors = smartVoltTextFieldColors()
                )

                CampoUsuario(
                    valor = cargo,
                    alCambiar = { cargo = it },
                    etiqueta = "Cargo",
                    ejemplo = "Ej. Operador eléctrico",
                    habilitado = !creando
                )

                EtiquetaFormulario("ROL DEL USUARIO")

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
                    EtiquetaFormulario("SUPERVISOR ASIGNADO")

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

                mensajeError?.let {
                    MensajeUsuarios(
                        mensaje = it,
                        esError = true
                    )
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
                shape = RoundedCornerShape(12.dp)
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
// CAMPO REUTILIZABLE
// --------------------------------------------------

@Composable
private fun CampoUsuario(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    ejemplo: String,
    habilitado: Boolean
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        placeholder = { Text(ejemplo) },
        singleLine = true,
        enabled = habilitado,
        shape = RoundedCornerShape(14.dp),
        colors = smartVoltTextFieldColors()
    )
}

@Composable
private fun EtiquetaFormulario(
    texto: String
) {
    Text(
        text = texto,
        color = VerdeUsuarios,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium
    )
}

// --------------------------------------------------
// SELECTOR DESPLEGABLE
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
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Borde)
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
                    color = TextoUsuarios
                )

                Text(
                    text = "⌄",
                    color = VerdeUsuarios,
                    fontWeight = FontWeight.Bold
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
    val colorEstado = if (usuario.activo) {
        VerdeExitoUsuarios
    } else {
        RojoErrorUsuarios
    }

    val iniciales = usuario.nombreCompleto
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoUsuarios
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            SuperficieUsuarios,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales.ifBlank { "U" },
                        color = VerdeUsuarios,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = usuario.nombreCompleto,
                        color = TextoUsuarios,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = usuario.id,
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    color = if (usuario.activo) {
                        FondoExitoUsuarios
                    } else {
                        FondoErrorUsuarios
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (usuario.activo) "ACTIVO" else "INACTIVO",
                        color = colorEstado,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }

            Surface(
                color = FondoUsuarios,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(11.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = usuario.cargo,
                        color = TextoUsuarios,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Rol: ${usuario.rol}",
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onEditar,
                        enabled = !actualizando,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Borde)
                    ) {
                        Text(
                            text = "Editar",
                            color = VerdeUsuarios,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = onEliminar,
                        enabled = !actualizando,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE8CACA))
                    ) {
                        Text(
                            text = "Eliminar",
                            color = RojoErrorUsuarios,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (actualizando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = VerdeUsuarios,
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
        shape = RoundedCornerShape(24.dp),
        containerColor = BlancoUsuarios,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Editar usuario",
                    color = TextoUsuarios,
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
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = smartVoltTextFieldColors()
                )

                CampoUsuario(
                    valor = nombre,
                    alCambiar = { nombre = it },
                    etiqueta = "Nombre completo",
                    ejemplo = "Nombre y apellido",
                    habilitado = true
                )

                CampoUsuario(
                    valor = cargo,
                    alCambiar = { cargo = it },
                    etiqueta = "Cargo",
                    ejemplo = "Cargo del trabajador",
                    habilitado = true
                )

                EtiquetaFormulario("ROL DEL USUARIO")

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
                    EtiquetaFormulario("SUPERVISOR ASIGNADO")

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

                mensajeError?.let {
                    MensajeUsuarios(
                        mensaje = it,
                        esError = true
                    )
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
                shape = RoundedCornerShape(12.dp)
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
