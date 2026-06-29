package com.umbrellastella.dickalogistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umbrellastella.dickalogistics.components.DickaNavigationBar
import com.umbrellastella.dickalogistics.ui.theme.DickaLogisticsTheme
import com.umbrellastella.dickalogistics.ui.theme.LoginScreen
import com.umbrellastella.dickalogistics.ui.theme.SplashScreen
import com.umbrellastella.dickalogistics.home.InicioScreen
import com.umbrellastella.dickalogistics.home.ProductosScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DickaLogisticsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controlador oficial de navegación
                    val navController = rememberNavController()

                    //Leemos la ruta actual para saber cuándo ocultar la barra
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val rutaActual = navBackStackEntry?.destination?.route

                    // 🛠️ Metemos el Scaffold AQUÍ adentro para no romper la Surface
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            // Si es splash o login, NO se muestra la barra.
                            // Solo aparecerá cuando entremos a "inicio" u otras pantallas de rol.
                            if (rutaActual != "splash" && rutaActual != "login" && rutaActual != null) {
                                DickaNavigationBar(navController = navController)
                            }
                        }
                    ) { paddingValues -> // Este padding evita que la barra tape tus listas

                        // Tu mapa de rutas (NavHost) exactamente igual, solo adaptando el modifier
                        NavHost(
                            navController = navController,
                            startDestination = "splash", // Inicia en el Splash
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues) // Aplica el espacio de la barra de forma correcta
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
                                        navController.navigate("inicio") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("inicio") {
                                InicioScreen()
                            }

                            composable("productos") {
                                ProductosScreen()
                            }



                            // Registra aquí tus rutas futuras si se agregan a la barra:
                            // composable("escanear") { EscanearScreen() }
                            // composable("admin_panel") { AdminPanelScreen() }
                        }
                    }
                }
            }
        }
    }
}