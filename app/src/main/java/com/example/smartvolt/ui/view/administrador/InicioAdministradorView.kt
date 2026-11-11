
package com.example.smartvolt.ui.view.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartvolt.datos.local.AppDataBase
import com.example.smartvolt.datos.repositorio.FaenaRepositorio
import com.example.smartvolt.datos.repositorio.UsuarioRepositorio
import com.example.smartvolt.ui.components.PanelPrincipalView
import com.example.smartvolt.ui.theme.AzulOscuro
import com.example.smartvolt.ui.theme.Borde
import com.example.smartvolt.ui.theme.TextoSecundario
import com.example.smartvolt.ui.theme.VerdeVolt
import com.example.smartvolt.viewmodel.FaenaViewModel
import com.example.smartvolt.viewmodel.FaenaViewModelFactory
import com.example.smartvolt.viewmodel.UsuarioViewModel
import com.example.smartvolt.viewmodel.UsuarioViewModelFactory

private val VerdePrincipalAdmin = Color(0xFF1E5147)
private val FondoAdmin = Color(0xFFF4F6F2)
private val SuperficieAdmin = Color(0xFFE9EFE9)
private val TextoPrincipalAdmin = Color(0xFF172522)
private val BlancoAdmin = Color.White

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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ENCABEZADO DE BIENVENIDA
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "CENTRO DE CONTROL",
                color = TextoSecundario,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "Panel de administración",
                color = TextoPrincipalAdmin,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Administra los recursos principales de la plataforma.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // TARJETA PRINCIPAL
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AzulOscuro,
                            VerdePrincipalAdmin
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Surface(
                    color = VerdeVolt.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "GESTIÓN CENTRAL",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        color = VerdeVolt,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Text(
                    text = "Todo bajo\ncontrol.",
                    color = BlancoAdmin,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "Administra personas, faenas y recursos desde un solo lugar.",
                    color = Color(0xFFD0DED5),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // ACCESOS
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Módulos de administración",
                color = TextoPrincipalAdmin,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Selecciona una sección para continuar.",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TarjetaAccesoAdministrador(
            numero = "01",
            titulo = "Gestión de usuarios",
            descripcion = "Consulta cuentas y administra el acceso del personal.",
            onClick = { onCambiarSeccion(1) }
        )

        TarjetaAccesoAdministrador(
            numero = "02",
            titulo = "Gestión de faenas",
            descripcion = "Organiza las faenas y sus asignaciones.",
            onClick = { onCambiarSeccion(2) }
        )

        TarjetaAccesoAdministrador(
            numero = "03",
            titulo = "Configuración",
            descripcion = "Revisa las opciones generales del sistema.",
            onClick = { onCambiarSeccion(3) }
        )
    }
}

@Composable
private fun TarjetaAccesoAdministrador(
    numero: String,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BlancoAdmin
        ),
        border = BorderStroke(1.dp, Borde),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = SuperficieAdmin,
                shape = RoundedCornerShape(13.dp)
            ) {
                Text(
                    text = numero,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 13.dp
                    ),
                    color = VerdePrincipalAdmin,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = titulo,
                    color = TextoPrincipalAdmin,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = descripcion,
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                color = AzulOscuro,
                shape = RoundedCornerShape(11.dp)
            ) {
                Text(
                    text = "→",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    color = VerdeVolt,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "ADMINISTRACIÓN",
            color = TextoSecundario,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = titulo,
            color = TextoPrincipalAdmin,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = descripcion,
            color = TextoSecundario,
            style = MaterialTheme.typography.bodyMedium
        )

        Surface(
            color = SuperficieAdmin,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "MÓDULO EN PREPARACIÓN",
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
                color = VerdePrincipalAdmin,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
