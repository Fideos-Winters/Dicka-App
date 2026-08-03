package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.viewmodel.MapaRackViewModel
import com.umbrellastella.dickalogistics.data.models.PosicionRack

@Composable
fun MapaRackScreen(viewModel: MapaRackViewModel) {
    val mapaCompleto by viewModel.mapaCompleto.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        when {
            // 1️⃣ Estado de carga (mientras el ViewModel reintenta en silencio)
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF008080))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Consultando el mapa del almacén...", color = Color.Gray)
                }
            }

            // 2️⃣ Estado de error o lista vacía: Altar con botón de reintento
            isError || (mapaCompleto ?: emptyList()).isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "La conexión con el almacén se cortó inesperadamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.cargarMapaCompleto() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008080))
                    ) {
                        Text(text = "Reintentar conexión", color = Color.White)
                    }
                }
            }

            // 3️⃣ El mapa cargado con éxito (Cuadrícula segura sin doble scroll)
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(mapaCompleto ?: emptyList()) { rackData ->
                        // Encabezado del Rack
                        Text(
                            text = "Rack ${rackData.rack}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF008080))
                                .padding(8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Recorremos cada Nivel del Rack
                        rackData.niveles.forEach { nivelData ->
                            Text(
                                text = "Nivel ${nivelData.nivel}",
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.DarkGray
                            )

                            // 🟩 Agrupamos las posiciones de 4 en 4 para formar filas seguras
                            val filas = (nivelData.ubicaciones ?: emptyList()).chunked(4)

                            filas.forEach { filaItems ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    filaItems.forEach { posRack: PosicionRack ->
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(70.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (posRack.estado.equals("Ocupado", ignoreCase = true)) {
                                                    Color(0xFFB71C1C) // Rojo para ocupado
                                                } else {
                                                    Color(0xFF00695C) // Verde/Cian para disponible
                                                }
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(4.dp),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = posRack.codigo,
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )

                                                // 🟩 Leemos el pallet directamente desde la posición purificada
                                                if (posRack.palletNumero != null && posRack.palletNumero > 0) {
                                                    Text(
                                                        text = "P:${posRack.palletNumero}",
                                                        fontSize = 9.sp,
                                                        color = Color.Yellow
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Si la fila tiene menos de 4 celdas, rellenamos con espacios invisibles
                                    repeat(4 - filaItems.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}