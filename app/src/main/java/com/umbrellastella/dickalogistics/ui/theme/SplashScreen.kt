package com.umbrellastella.dickalogistics.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
// Asegúrate de importar la referencia de tus recursos drawable
import com.umbrellastella.dickalogistics.R

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    var isButtonClicked by remember { mutableStateOf(false) }

    // Animación de zoom al presionar el botón
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isButtonClicked) 3.0f else 1.0f,
        animationSpec = tween(durationMillis = 800),
        label = "zoomAnimation"
    )

    // Efecto de retraso para dar tiempo a la animación antes de cambiar de pantalla
    LaunchedEffect(isButtonClicked) {
        if (isButtonClicked) {
            delay(800)
            onNavigateToLogin()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. EL FONDO (Azul Corporativo con animación de Zoom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleAnimation)
                .background(MaterialTheme.colorScheme.primary)
        )

        // 2. CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // -- ZONA DEL LOGOTIPO OFICIAL --
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Renderizado de la imagen del Logo
                Image(
                    painter = painterResource(id = R.drawable.logo), // Reemplaza "logo_dicka" si tu archivo se llama diferente
                    contentDescription = "Logo Oficial de Dicka Logistics",
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // Ocupa el 70% del ancho para un balance visual perfecto
                        .heightIn(max = 180.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Texto secundario de la marca
                Text(
                    text = "DICKA LOGISTICS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp // Espaciado elegante de letras
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sistema de Gestión de Almacén",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // -- BOTÓN DE ACCIÓN --
            Button(
                onClick = { isButtonClicked = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}