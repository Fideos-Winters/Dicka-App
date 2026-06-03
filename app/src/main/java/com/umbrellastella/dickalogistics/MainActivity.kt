package com.umbrellastella.dickalogistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umbrellastella.dickalogistics.ui.theme.DickaLogisticsTheme
import com.umbrellastella.dickalogistics.ui.theme.LoginScreen
import com.umbrellastella.dickalogistics.ui.theme.SplashScreen

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

                    // Mapa de rutas (NavHost)
                    NavHost(
                        navController = navController,
                        startDestination = "splash" // Inicia en el Splash
                    ) {

                        composable("splash") {
                            SplashScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        // Esto borra el splash del historial para que al dar "Atrás", la app se cierre
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen()
                        }
                    }
                }
            }
        }
    }
}