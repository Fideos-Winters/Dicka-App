package com.umbrellastella.dickalogistics.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    var isButtonClicked by remember { mutableStateOf(false) }

    // zoom mamalon
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isButtonClicked) 3.0f else 1.0f,
        animationSpec = tween(durationMillis = 800),
        label = "zoomAnimation"
    )

    // Efecto mamalon
    LaunchedEffect(isButtonClicked) {
        if (isButtonClicked) {
            delay(800)
            onNavigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. EL FONDO (Azul Corporativo)
        // Usamos el color "primary" que en tu Theme.kt ya configuramos como DickaBlue
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleAnimation)
                .background(MaterialTheme.colorScheme.primary)
        )

        // 2. EL CONTENIDO (Blanco puro para máximo contraste)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // -- ZONA DEL LOGOTIPO --
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Un cuadro blanco con bordes muy redondeados simulando el icono de la app
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Letra inicial del logo en azul
                    Text(
                        text = "D",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nombre de la empresa debajo del logo
                Text(
                    text = "DICKA LOGISTICS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp // Separa las letras para un toque más premium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // -- Botonzo --
            Button(
                onClick = { isButtonClicked = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, // Botón blanco
                    contentColor = MaterialTheme.colorScheme.primary // Letras azules
                )
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}