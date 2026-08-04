package com.umbrellastella.dickalogistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.SessionManager
import com.umbrellastella.dickalogistics.ui.theme.DickaLogisticsTheme
import com.umbrellastella.dickalogistics.ui.screens.LoginScreen
import com.umbrellastella.dickalogistics.ui.theme.SplashScreen
import com.umbrellastella.dickalogistics.ui.navigation.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DickaLogisticsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val sessionManager = remember { SessionManager(context) }

                    // 1️⃣ Leemos de forma reactiva el token guardado en el DataStore
                    val tokenGuardado by sessionManager.tokenFlow.collectAsState(initial = null)

                    // Estado temporal para saber si ya cargó la preferencia inicial y evitar parpadeos
                    var isLoadingSession by remember { mutableStateOf(true) }
                    var rolUsuario by remember { mutableStateOf("ADMIN") } // Por defecto ADMIN o el que necesites leer

                    // Verificamos el estado al arrancar
                    LaunchedEffect(tokenGuardado) {
                        if (tokenGuardado != null) {
                            RetrofitClient.tokenAcceso = tokenGuardado
                        }
                        isLoadingSession = false
                    }

                    // Si está checando el almacenamiento, mostramos un splash de carga limpio
                    if (isLoadingSession) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                        return@Surface
                    }

                    val navController = rememberNavController()

                    // 2️⃣ Definimos la ruta inicial inteligente: Si hay token, salta directo al main_app
                    val rutaInicioDinamica = if (!tokenGuardado.isNullOrEmpty()) "main_app" else "splash"

                    NavHost(
                        navController = navController,
                        startDestination = rutaInicioDinamica
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("main_app") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("main_app") {
                            // 3️⃣ Aquí pasamos la función de onLogoutSuccess para cerrar sesión correctamente
                            MainScreen(
                                userRole = rolUsuario,
                                onLogoutSuccess = {
                                    navController.navigate("login") {
                                        // Destruimos la pila de navegación para que no puedan regresar al panel
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}