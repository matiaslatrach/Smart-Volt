
package com.example.smartvolt.ui.view.operario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartvolt.modelo.Usuario

private val VerdePerfil = Color(0xFF142B2A)
private val VerdeLimaPerfil = Color(0xFFC7E56A)
private val TextoPerfil = Color(0xFF172522)
private val TextoSecundarioPerfil = Color(0xFF718078)

@Composable
fun PerfilOperarioView(usuario: Usuario) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Encabezado.
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Mi perfil",
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPerfil
            )

            Text(
                text = "Consulta tu información personal y laboral.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundarioPerfil
            )
        }

        // Identidad del operario.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = VerdePerfil
        ) {
            Row(
                modifier = Modifier.padding(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = VerdeLimaPerfil
                    ) {}

                    Text(
                        text = usuario.nombreCompleto
                            .trim()
                            .split(Regex("\\s+"))
                            .filter { it.isNotBlank() }
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            .joinToString(""),
                        style = MaterialTheme.typography.headlineSmall,
                        color = VerdePerfil
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = usuario.nombreCompleto,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Text(
                        text = "OPERARIO SMARTVOLT",
                        style = MaterialTheme.typography.labelMedium,
                        color = VerdeLimaPerfil
                    )

                    Text(
                        text = "Identificador: ${usuario.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD0DED5)
                    )
                }
            }
        }

        // Información personal y laboral.
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Información personal",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPerfil
                )

                InformacionPerfil(
                    etiqueta = "Nombre completo",
                    valor = usuario.nombreCompleto
                )

                InformacionPerfil(
                    etiqueta = "Identificador",
                    valor = usuario.id
                )

                InformacionPerfil(
                    etiqueta = "Cargo",
                    valor = usuario.cargo
                )

                InformacionPerfil(
                    etiqueta = "Rol del sistema",
                    valor = usuario.rol
                )
            }
        }

        // Identificación del sistema.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFE9EFE9)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "SMARTVOLT",
                    style = MaterialTheme.typography.labelLarge,
                    color = VerdePerfil
                )

                Text(
                    text = "Gestión de faenas y control de asistencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundarioPerfil
                )
            }
        }
    }
}

@Composable
private fun InformacionPerfil(
    etiqueta: String,
    valor: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundarioPerfil
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            color = TextoPerfil
        )

        Spacer(modifier = Modifier.size(1.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE9EFE9),
            shape = RoundedCornerShape(50.dp)
        ) {
            Spacer(modifier = Modifier.size(1.dp))
        }
    }
}
