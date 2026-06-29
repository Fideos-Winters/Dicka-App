package com.umbrellastella.dickalogistics.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.umbrellastella.dickalogistics.data.SesionUsuario
import com.umbrellastella.dickalogistics.navigation.ItemNavegacion

// Reutilizamos tus colores oficiales
val DickaBlue = Color(0xFF0F172A)
val DickaOrange = Color(0xFFF97316)

@Composable
fun DickaNavigationBar(navController: NavController) {
    // Obtenemos los destinos disponibles
    val items = listOf(
        ItemNavegacion.Inicio,
        ItemNavegacion.Productos,
        ItemNavegacion.Escanear,
        ItemNavegacion.AdminPanel
    )

    // Filtramos los ítems: Solo se quedan los que incluyan el rol del usuario actual
    val itemsFiltrados = items.filter { it.rolesPermitidos.contains(SesionUsuario.rolActual) }

    // Saber en qué pantalla estamos parados actualmente para encender el botón correcto
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = DickaBlue // Fondo azul oscuro corporativo
    ) {
        itemsFiltrados.forEach { item ->
            val seleccionado = rutaActual == item.ruta

            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    if (rutaActual != item.ruta) {
                        navController.navigate(item.ruta) {
                            // Evita acumular pantallas en el backstack al navegar entre pestañas
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(
                        text = item.titulo,
                        color = if (seleccionado) DickaOrange else Color.White.copy(alpha = 0.6f)
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icono,
                        contentDescription = item.titulo,
                        tint = if (seleccionado) DickaOrange else Color.White.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = DickaBlue // Quita el óvalo de fondo predeterminado o ponlo al mismo color del fondo
                )
            )
        }
    }
}