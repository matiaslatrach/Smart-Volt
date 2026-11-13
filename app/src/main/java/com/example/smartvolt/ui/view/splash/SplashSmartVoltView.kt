package com.example.smartvolt.ui.view.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartvolt.ui.theme.BosqueProfundo
import com.example.smartvolt.ui.theme.VerdePrincipal
import com.example.smartvolt.ui.theme.VerdeVolt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Presentación de inicio. No modifica la sesión ni la navegación del usuario. */
@Composable
fun SplashSmartVoltView(onFinalizar: () -> Unit) {
    val alFinalizar by rememberUpdatedState(onFinalizar)
    val aparicion = remember { Animatable(0f) }
    val progreso = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Entrada suave del logotipo y barra de progreso; dura cerca de dos segundos.
        coroutineScope {
            launch {
                aparicion.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(650, easing = FastOutSlowInEasing)
                )
            }
            launch {
                progreso.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1850, easing = LinearEasing)
                )
            }
        }
        aparicion.animateTo(0f, animationSpec = tween(250))
        alFinalizar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(BosqueProfundo, VerdePrincipal))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .graphicsLayer {
                    alpha = aparicion.value
                    val escala = 0.92f + 0.08f * aparicion.value
                    scaleX = escala
                    scaleY = escala
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(206.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape)
                )
                Surface(
                    modifier = Modifier.size(104.dp),
                    shape = CircleShape,
                    color = VerdeVolt,
                    shadowElevation = 10.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = BosqueProfundo,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SMART",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = Color.White
                )
                Text(
                    text = "VOLT",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = VerdeVolt
                )
            }

            Spacer(Modifier.height(9.dp))
            Text(
                text = "CONTROL DE ASISTENCIA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.72f)
            )

            Spacer(Modifier.height(38.dp))

            // Barra propia: no requiere una librería adicional de animación de UI.
            Box(
                modifier = Modifier
                    .width(156.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.17f), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progreso.value)
                        .height(4.dp)
                        .background(VerdeVolt, RoundedCornerShape(50))
                )
            }
        }

        Text(
            text = "ENERGÍA EN CADA JORNADA",
            fontSize = 10.sp,
            letterSpacing = 1.7.sp,
            color = Color.White.copy(alpha = 0.55f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
        )
    }
}
