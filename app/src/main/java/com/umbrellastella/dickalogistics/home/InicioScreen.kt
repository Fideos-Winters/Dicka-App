package com.umbrellastella.dickalogistics.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.viewmodel.DashboardUiState
import com.umbrellastella.dickalogistics.viewmodel.DashboardViewModel

// Definimos los colores directamente o los jalamos de tu Theme
val DickaBlue = Color(0xFF0F172A)
val DickaOrange = Color(0xFFF97316)
val DickaLightBackground = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DICKA LOGISTICS",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DickaBlue)
            )
        },
        containerColor = DickaLightBackground // Aplicamos tu fondo gris-azulado clarito
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = viewModel.uiState) {
                is DashboardUiState.Loading -> {
                    // Indicador de carga usando tu naranja de acción
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DickaOrange
                    )
                }

                is DashboardUiState.Success -> {
                    val datos = state.datos

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Sección de Tarjetas de Estadísticas (Kpis)
                        item {
                            Text(
                                text = "Resumen de Almacén",
                                style = MaterialTheme.typography.titleLarge,
                                color = DickaBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Tarjeta 1: Total Piezas (En Azul)
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = DickaBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Total Piezas", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("${datos.totalPiezasAlmacen}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Tarjeta 2: Ubicaciones (En Naranja)
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = DickaOrange),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Ubicaciones", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("${datos.ubicacionesOcupadas} QR", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Título del listado de inventario activo
                        item {
                            Text(
                                text = "Productos en Almacén",
                                style = MaterialTheme.typography.titleMedium,
                                color = DickaBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Renderizado de la lista real que viene del controlador de Laravel
                        items(datos.productosEnAlmacen) { producto ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = producto.producto,
                                            color = DickaBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "SKU: ${producto.sku} | Lote: ${producto.lote}",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Ubicación: ${producto.ubicacion}",
                                            color = DickaBlue,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    }

                                    // Indicador de cantidad al costado derecho destacado en fondo suave
                                    Box(
                                        modifier = Modifier
                                            .background(DickaLightBackground, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "${producto.piezas} pzs",
                                            color = DickaOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is DashboardUiState.Error -> {
                    // Pantalla de error con botón de acción Naranja
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.mensaje, color = Color.Red, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.cargarDatosDelDashboard() },
                            colors = ButtonDefaults.buttonColors(containerColor = DickaOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reintentar Conexión", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}