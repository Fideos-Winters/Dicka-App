package com.umbrellastella.dickalogistics.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.viewmodel.ProductoUiState
import com.umbrellastella.dickalogistics.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(viewModel: ProductoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CATÁLOGO DE PRODUCTOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DickaBlue)
            )
        },
        containerColor = DickaLightBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔍 Barra de Búsqueda inteligente
            OutlinedTextField(
                value = viewModel.textoBusqueda,
                onValueChange = { viewModel.actualizarBusqueda(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre, SKU o cliente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = DickaBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DickaOrange,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = DickaOrange
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Control de estados de la UI
            when (val state = viewModel.uiState) {
                is ProductoUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DickaOrange)
                    }
                }
                is ProductoUiState.Success -> {
                    if (viewModel.productosFiltrados.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron productos.", color = Color.Gray)
                        }
                    } else {
                        // Lista Deslizable de Productos
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.productosFiltrados) { producto ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = producto.nombre,
                                                color = DickaBlue,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.weight(1f)
                                            )

                                            // Etiqueta de la Categoría (Cliente)
                                            Box(
                                                modifier = Modifier
                                                    .background(DickaLightBackground, RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = producto.cliente,
                                                    color = DickaBlue,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "SKU: ${producto.sku}", color = DickaOrange, fontWeight = FontWeight.Medium, fontSize = 13.sp)

                                        if (producto.caracteristicas.isNotEmpty()) {
                                            Text(text = "Características: ${producto.caracteristicas}", color = Color.Gray, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is ProductoUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.mensaje, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.cargarProductos() },
                            colors = ButtonDefaults.buttonColors(containerColor = DickaOrange)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}