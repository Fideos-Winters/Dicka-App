package com.umbrellastella.dickalogistics.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp // Importante para el icono de salida
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umbrellastella.dickalogistics.data.models.ProductoModel
import com.umbrellastella.dickalogistics.home.ClientesScreen
import com.umbrellastella.dickalogistics.home.DashboardScreen
import com.umbrellastella.dickalogistics.home.ProductosScreen
import com.umbrellastella.dickalogistics.ui.screens.*
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.viewmodels.InventarioViewModel
import com.umbrellastella.dickalogistics.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userRole: String = "ADMIN",
    loginViewModel: LoginViewModel = viewModel(), // 1️⃣ Inyectamos el ViewModel de Login
    onLogoutSuccess: () -> Unit // 2️⃣ Avisamos al NavHost que nos mande al Login
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Inventario) }
    var userMenuExpanded by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<ProductoModel?>(null) }

    val inventarioViewModel: InventarioViewModel = viewModel()
    val ubicarViewModel: UbicarViewModel = viewModel()
    val mapaRackViewModel: MapaRackViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dicka Logistics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                actions = {
                    if (userRole == "ADMIN") {
                        IconButton(onClick = { currentScreen = Screen.Usuarios }) {
                            Icon(Icons.Default.AdminPanelSettings, "Usuarios", tint = Color.White)
                        }
                    }
                    Box {
                        IconButton(onClick = { userMenuExpanded = true }) {
                            Icon(Icons.Default.AccountCircle, "Menú", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        DropdownMenu(expanded = userMenuExpanded, onDismissRequest = { userMenuExpanded = false }) {
                            DropdownMenuItem(text = { Text("Actualizar Contraseña") }, onClick = { userMenuExpanded = false })
                            if (userRole == "ADMIN") {
                                DropdownMenuItem(
                                    text = { Text("Administrar Usuarios") },
                                    onClick = { currentScreen = Screen.Usuarios; userMenuExpanded = false }
                                )
                            }

                            Divider() // Una línea sutil para separar la salida

                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = Color.Red, fontWeight = FontWeight.Bold) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    userMenuExpanded = false
                                    // 3️⃣ Ejecutamos la función que limpia el DataStore
                                    loginViewModel.cerrarSesion {
                                        onLogoutSuccess() // Avisa que ya borró todo y navega al Login
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DickaBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { currentScreen = Screen.Dashboard },
                containerColor = if (currentScreen == Screen.Dashboard) Color(0xFF00223D) else DickaBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Dashboard, "Dashboard") }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp, modifier = Modifier.height(72.dp)) {
                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    ItemMenuInferior(Screen.Inventario, currentScreen == Screen.Inventario) { currentScreen = Screen.Inventario }
                    ItemMenuInferior(Screen.Productos, currentScreen == Screen.Productos) { currentScreen = Screen.Productos }
                    Spacer(modifier = Modifier.width(56.dp))
                    ItemMenuInferior(Screen.Ubicar, currentScreen == Screen.Ubicar) { currentScreen = Screen.Ubicar }
                    ItemMenuInferior(Screen.Clientes, currentScreen == Screen.Clientes) { currentScreen = Screen.Clientes }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                Screen.Inventario -> InventarioScreen(
                    viewModel = inventarioViewModel,
                    onNavigateToEntradas = { currentScreen = Screen.EntradaLotes },
                    onNavigateToSalidas = { currentScreen = Screen.SalidaLotes },
                    onProductoClick = { producto -> selectedProducto = producto; currentScreen = Screen.DesgloseLotes }
                )
                Screen.EntradaLotes -> EntradaLotesScreen(onBackToInventario = { currentScreen = Screen.Inventario }, viewModel = viewModel())
                Screen.SalidaLotes -> SalidaLotesScreen(onBackToInventario = { currentScreen = Screen.Inventario }, viewModel = viewModel())
                Screen.DesgloseLotes -> DesgloseLotesScreen(
                    productoGlance = selectedProducto ?: ProductoModel("", "No seleccionado", "", "", ""),
                    viewModel = inventarioViewModel
                )
                Screen.Ubicar -> UbicarScreen(
                    viewModel = ubicarViewModel,
                    onNavigateToMapa = { currentScreen = Screen.MapaRack }
                )
                Screen.MapaRack -> MapaRackScreen(viewModel = mapaRackViewModel)
                Screen.Dashboard -> DashboardScreen(viewModel = viewModel())
                Screen.Productos -> ProductosScreen(viewModel = viewModel())
                Screen.Clientes -> ClientesScreen(viewModel = viewModel())
                Screen.Usuarios -> UsuariosScreen()
            }
        }
    }
}

@Composable
fun ItemMenuInferior(screen: Screen, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(4.dp)) {
        screen.icon?.let { Icon(it, screen.title, tint = if (isSelected) DickaBlue else Color(0xFF64748B), modifier = Modifier.size(24.dp)) }
        Text(screen.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) DickaBlue else Color(0xFF64748B))
    }
}