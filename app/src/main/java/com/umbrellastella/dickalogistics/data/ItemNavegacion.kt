package com.umbrellastella.dickalogistics.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ItemNavegacion(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector,
    val rolesPermitidos: List<String> // Aquí filtramos quién puede ver qué
) {
    object Inicio : ItemNavegacion(
        ruta = "inicio",
        titulo = "Inicio",
        icono = Icons.Default.Home,
        rolesPermitidos = listOf("Administrador", "Auxiliar", "Montacarguista") // Todos ven el inicio
    )

    object Productos : ItemNavegacion(
        ruta = "productos",
        titulo = "Productos",
        icono = Icons.Default.Inventory, // Icono de caja de inventario
        rolesPermitidos = listOf("Administrador", "Auxiliar", "Montacarguista") // Ajusta los roles según tu lógica
    )

    object Escanear : ItemNavegacion(
        ruta = "escanear",
        titulo = "Escanear QR",
        icono = Icons.Default.QrCodeScanner,
        rolesPermitidos = listOf("Montacarguista", "Auxiliar", "Administrador")
    )

    object AdminPanel : ItemNavegacion(
        ruta = "admin_panel",
        titulo = "Gestión",
        icono = Icons.Default.SupervisorAccount,
        rolesPermitidos = listOf("Administrador") // Solo el jefe supremo lo ve
    )
}