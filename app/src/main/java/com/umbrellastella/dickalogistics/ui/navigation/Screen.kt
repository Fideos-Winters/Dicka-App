package com.umbrellastella.dickalogistics.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Inventario : Screen("inventario", "Inventario", Icons.Default.Inventory)
    object Productos : Screen("productos", "Productos", Icons.Default.Assignment)
    object Ubicar : Screen("ubicar", "Ubicar", Icons.Default.LocationOn)
    object Clientes : Screen("clientes", "Clientes", Icons.Default.Group)
    object Usuarios : Screen("usuarios", "Administrar Usuarios", Icons.Default.AdminPanelSettings)

    object EntradaLotes : Screen("entrada_lotes", "Entrada de Lotes", null)
    object SalidaLotes : Screen("salida_lotes", "Salida de Lotes", null)

    object MapaRack : Screen("mapa_rack", "Mapa de Rack", null)
    object DesgloseLotes : Screen("desglose_lotes/{sku}", "Desglose de Lotes", null)
}