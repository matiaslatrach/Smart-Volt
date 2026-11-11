package com.example.smartvolt.ui.view.operario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartvolt.modelo.Usuario

@Composable
fun PerfilOperarioView(usuario: Usuario) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Consulta tu información personal y laboral.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Información del usuario",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Nombre completo: ${usuario.nombreCompleto}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Identificador: ${usuario.id}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Cargo: ${usuario.cargo}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Rol: ${usuario.rol}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}