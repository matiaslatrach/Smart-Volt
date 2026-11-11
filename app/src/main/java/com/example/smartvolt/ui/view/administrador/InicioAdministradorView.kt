package com.example.smartvolt.ui.view.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.UsuarioRepositorio
import com.example.smartvolt.ui.components.PanelPrincipalView
import com.example.smartvolt.ui.theme.AzulOscuro
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.viewmodel.UsuarioViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModelFactory
import com.example.smartvolt.datos.repositorio.FaenaRepositorio
import com.example.smartvolt.viewmodel.FaenaViewModel
import com.example.smartvolt.viewmodel.FaenaViewModelFactory

@Composable
fun InicioAdministradorView(
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current

    val usuarioDAO = remember {
        AppDataBase.obtenerBaseDatos(context).usuarioDAO()
    }

    val repositorio = remember(usuarioDAO) {
        UsuarioRepositorio(usuarioDAO)
    }

    val factory = remember(repositorio) {
        UsuarioViewModelFactory(repositorio)
    }

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = factory
    )

    val faenaDAO = remember {
        AppDataBase.obtenerBaseDatos(context).faenaDAO()
    }

    val faenaRepositorio = remember(faenaDAO) {
        FaenaRepositorio(faenaDAO)
    }

    val faenaFactory = remember(faenaRepositorio) {
        FaenaViewModelFactory(faenaRepositorio)
    }

    val faenaViewModel: FaenaViewModel = viewModel(
        factory = faenaFactory
    )

    PanelPrincipalView(
        nombreRol = "ADMINISTRADOR",
        titulo = "Administración de SmartVolt",
        subtitulo = "Gestiona usuarios, parámetros y recursos del sistema.",
        modulos = listOf(
            "Gestión de usuarios",
            "Gestión de faenas",
            "Configuración del sistema"
        ),
        onCerrarSesion = onCerrarSesion,
        contenidoPersonalizado = { indice, cambiarSeccion ->
            when (indice) {
                0 -> ContenidoInicioAdministrador(
                    onCambiarSeccion = cambiarSeccion
                )

                1 -> GestionUsuariosView(
                    usuarioViewModel = usuarioViewModel
                )

                2 -> GestionFaenasView(
                    faenaViewModel = faenaViewModel,
                    usuarioViewModel = usuarioViewModel
                )

                3 -> SeccionAdministradorPendiente(
                    titulo = "Configuración del sistema",
                    descripcion = "Este espacio estará destinado a los parámetros generales de SmartVolt."
                )
            }
        }
    )
}

@Composable
private fun ContenidoInicioAdministrador(
    onCambiarSeccion: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "CENTRO DE CONTROL",
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Panel de administración",
                color = AzulOscuro,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Administra los recursos principales de la plataforma.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        TarjetaAccesoAdministrador(
            titulo = "Gestión de usuarios",
            descripcion = "Consulta las cuentas y controla el acceso del personal.",
            onClick = { onCambiarSeccion(1) }
        )

        TarjetaAccesoAdministrador(
            titulo = "Gestión de faenas",
            descripcion = "Accede al módulo de administración de faenas.",
            onClick = { onCambiarSeccion(2) }
        )

        TarjetaAccesoAdministrador(
            titulo = "Configuración",
            descripcion = "Revisa las opciones generales del sistema.",
            onClick = { onCambiarSeccion(3) }
        )
    }
}

@Composable
private fun TarjetaAccesoAdministrador(
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Borde)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = titulo,
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = descripcion,
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                color = AzulOscuro,
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(
                    text = "→",
                    color = VerdeVolt,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun SeccionAdministradorPendiente(
    titulo: String,
    descripcion: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "ADMINISTRACIÓN",
            color = TextoSecundario,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = titulo,
            color = AzulOscuro,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = descripcion,
            color = TextoSecundario,
            style = MaterialTheme.typography.bodyMedium
        )

        Surface(
            color = Color(0xFFF0F2F5),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "MÓDULO EN PREPARACIÓN",
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                color = TextoSecundario,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
