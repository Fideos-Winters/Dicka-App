package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.data.models.LoteUbicacionModel
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.theme.DickaOrange
import com.umbrellastella.dickalogistics.viewmodel.UbicarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignarUbicacionBottomSheet(
    lote: LoteUbicacionModel,
    viewModel: UbicarViewModel,
    onDismiss: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    // Al abrir el menú manual, disparamos la búsqueda en Laravel
    LaunchedEffect(lote) {
        val targetId = lote.loteId ?: lote.id
        viewModel.cargarRecomendaciones(targetId)
    }

    ModalBottomSheet(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = lote.getNombreProducto(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue
            )
            Text(
                text = "Folio: ${lote.getFolio()} | Lote: ${lote.getCodigoLote()}",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text(
                text = "📍 Racks Sugeridos para Acomodo:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DickaBlue
            )

            Spacer(Modifier.height(8.dp))

            val recomendaciones = viewModel.listaUbicacionesDisponibles

            // ================= Para los lotes que erroneos =================
            if (viewModel.isLoadingRecomendaciones) {
                // 1. Si sigue consultando a Laravel, mostramos la ruedita
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DickaOrange)
                }
            } else if (recomendaciones.isEmpty()) {
                // 2. Si Laravel terminó y no hay lugares válidos, mostramos la alerta roja
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Text(
                        text = "⚠️ Laravel sugirió racks que ya están ocupados o no hay espacio disponible. Revisa la base de datos.",
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // 3. Si hay lugares disponibles, pintamos los botones naranjas
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recomendaciones) { ubicacionSugerida ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ubicacionSugerida,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = DickaBlue
                                )
                                Button(
                                    onClick = { onConfirmar(ubicacionSugerida) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DickaOrange)
                                ) {
                                    Text("Acomodar Aquí")
                                }
                            }
                        }
                    }
                }
            }
            // ================================================================
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun UbicacionItemRow(ubicacion: String, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF008080)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = ubicacion,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF334155)
            )
        }
    }
}