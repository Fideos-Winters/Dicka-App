package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umbrellastella.dickalogistics.data.models.ProductoModel
import com.umbrellastella.dickalogistics.ui.viewmodels.InventarioUiState
import com.umbrellastella.dickalogistics.ui.viewmodels.InventarioViewModel

private val DickaBlue = Color(0xFF005691)
private val DickaDeepBlue = Color(0xFF003359)
private val DickWhite = Color(0xFFFFFFFF)
private val BackgroundGray = Color(0xFFF0F4F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    viewModel: InventarioViewModel,
    onNavigateToEntradas: () -> Unit,
    onNavigateToSalidas: () -> Unit,
    onProductoClick: (ProductoModel) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showPnc by remember { mutableStateOf(false) }

    if (showPnc) {
        PncScreen(onBack = { showPnc = false })
    } else {
        Column(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
            // Tarjeta de Totalización Superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(DickaBlue, DickaDeepBlue)),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(bottom = 40.dp, start = 20.dp, end = 20.dp, top = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DickWhite),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Total Piezas en Almacén", color = Color(0xFF64748B), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(text = "233,236", color = DickaBlue, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Accesos rápidos: PNC, Entradas y Salidas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BotonAccionElegante(titulo = "Entradas", icono = Icons.Default.Input, modifier = Modifier.weight(1f), onClick = onNavigateToEntradas)
                        BotonAccionElegante(titulo = "Salidas", icono = Icons.Default.Output, modifier = Modifier.weight(1f), onClick = onNavigateToSalidas)
                    }
                }

                Text(
                    text = "Estado del Inventario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DickaBlue,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    placeholder = { Text("Buscar por SKU o Producto...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DickaBlue) },
                    trailingIcon = { if (searchQuery.isNotEmpty()) { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray) } } },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = Color(0xFFCBD5E1), focusedContainerColor = DickWhite, unfocusedContainerColor = DickWhite),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                when (val currentState = state) {
                    is InventarioUiState.Cargando -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = DickaBlue) }
                    is InventarioUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(text = "Error: ${currentState.mensaje}", color = Color.Red, fontSize = 14.sp); Button(onClick = { viewModel.cargarInventario() }) { Text("Reintentar") } } }
                    is InventarioUiState.Exito -> {
                        val productosFiltrados = currentState.productos.filter { it.nombreProducto.contains(searchQuery, ignoreCase = true) || it.sku.contains(searchQuery, ignoreCase = true) }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
                            items(productosFiltrados) { producto -> ProductoDetalleItem(producto) { onProductoClick(producto) } }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BotonAccionElegante(titulo: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(75.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DickWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = icono, contentDescription = null, tint = DickaBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = titulo, fontWeight = FontWeight.Bold, color = DickaBlue, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProductoDetalleItem(producto: ProductoModel, onItemClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onItemClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = DickWhite), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = producto.nombreProducto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "SKU: ${producto.sku}", fontSize = 12.sp, color = Color(0xFF64748B))
                }
                Text(text = producto.totalPiezas, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}