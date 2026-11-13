
package com.example.smartvolt.ui.view.login

import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.TextoPrincipal
import com.example.smartvolt.ui.theme.Fondo
import com.example.smartvolt.ui.theme.BosqueProfundo
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.Blanco
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.smartvolt.modelo.Usuario
import com.example.smartvolt.ui.theme.smartVoltTextFieldColors
import com.example.smartvolt.viewmodel.LoginViewModel

@Composable
fun LoginView(
    viewModel: LoginViewModel,
    onLoginExitoso: (Usuario) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ENCABEZADO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BosqueProfundo, VerdePrincipal)
                    )
                )
                .padding(horizontal = 26.dp, vertical = 30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = VerdeVolt.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "⚡",
                            modifier = Modifier.padding(12.dp),
                            color = VerdeVolt,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "SMARTVOLT",
                            color = Blanco,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "GESTIÓN DE FAENAS",
                            color = Color(0xFFD0DED5),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "v1.0",
                        color = VerdeVolt,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(
                    color = Blanco.copy(alpha = 0.18f)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "PLATAFORMA DE OPERACIONES",
                        color = VerdeVolt,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Tu jornada\ncomienza aquí.",
                        color = Blanco,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Control y gestión de asistencia en faenas.",
                        color = Color(0xFFD0DED5),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = VerdeVolt.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "✓",
                            color = VerdeVolt,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "Acceso seguro",
                            color = Blanco,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Solo personal autorizado",
                            color = Color(0xFFD0DED5),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // FORMULARIO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = "IDENTIFICACIÓN DE USUARIO",
                color = VerdePrincipal,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Iniciar sesión",
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa tus credenciales para acceder a la plataforma.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(26.dp))

            // ID
            Text(
                text = "ID de trabajador",
                color = TextoPrincipal,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.id,
                onValueChange = { viewModel.onIdChanged(it) },
                placeholder = {
                    Text("Ej. OP-001")
                },
                leadingIcon = {
                    Text(
                        text = "ID",
                        color = VerdePrincipal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = smartVoltTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CONTRASEÑA
            Text(
                text = "Contraseña",
                color = TextoPrincipal,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = {
                    Text("Ingresa tu contraseña")
                },
                leadingIcon = {
                    Text(
                        text = "●",
                        color = VerdePrincipal,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                colors = smartVoltTextFieldColors()
            )

            // ERROR
            if (state.mensajeError != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = Color(0xFFFCEAEA),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.mensajeError!!,
                        color = Color(0xFFC64B4B),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // BOTÓN
            Button(
                onClick = {
                    viewModel.validarYIngresar { usuario ->
                        onLoginExitoso(usuario)
                    }
                },
                enabled = !state.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BosqueProfundo,
                    contentColor = Blanco,
                    disabledContainerColor = VerdePrincipal
                )
            ) {
                if (state.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = VerdeVolt,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Ingresar",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "→",
                        color = VerdeVolt,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            HorizontalDivider(color = Borde)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SMARTVOLT v1.0",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "GESTIÓN DE FAENAS",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
