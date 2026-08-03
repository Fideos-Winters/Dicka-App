package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.data.models.ProductoModel
import com.umbrellastella.dickalogistics.ui.viewmodels.InventarioViewModel

@Composable
fun DesgloseLotesScreen(
    productoGlance: ProductoModel,
    viewModel: InventarioViewModel, // Recibe el ViewModel existente
    modifier: Modifier = Modifier
) {
    val azulDicka = Color(0xFF00529B)
    val fondoOscuroLista = Color(0xFF1E293B)
    val blancoFondo = Color(0xFFFFFFFF)

    // Observar los estados del ViewModel
    val listaLotes by viewModel.lotesProducto.collectAsState()
    val cargando by viewModel.cargandoLotes.collectAsState()
    val error by viewModel.errorLotes.collectAsState()

    // Disparar consulta HTTP a Laravel de forma automática usando el SKU del producto
    LaunchedEffect(productoGlance.sku) {
        viewModel.cargarLotesPorProducto(productoGlance.sku)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(blancoFondo)
    ) {
        // Tarjeta de encabezado con los datos del producto
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = azulDicka),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = productoGlance.nombreProducto,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = productoGlance.totalPiezas,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "SKU: ${productoGlance.sku}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(text = "Cliente: ${productoGlance.cliente}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }

        // Listado inferior dinámico con control de estado de red
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(fondoOscuroLista)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (cargando) {
                CircularProgressIndicator(color = Color(0xFF38BDF8))
            } else if (error != null) {
                Text(
                    text = error!!,
                    color = Color(0xFFF87171),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(listaLotes) { lote ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Folio: # ${lote.folio}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${lote.cantidad} ${lote.unidadMedida}", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Lote: ${lote.lote}", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Palets: ${lote.numPalets} (Pzas/Palet: ${lote.piezasPalet})", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text(text = "Caducidad: ${lote.caducidad}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Estado: ${lote.ubicacion}",
                                    color = if (lote.ubicacion.lowercase() == "ubicado") Color(0xFF4ADE80) else Color(0xFFF87171),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}