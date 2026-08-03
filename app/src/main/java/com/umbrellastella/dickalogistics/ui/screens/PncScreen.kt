package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mockup data para visualizar el listado de PNC
data class PncProducto(val sku: String, val nombre: String, val motivo: String, val fecha: String)

@Composable
fun PncScreen(onBack: () -> Unit) {
    // Aquí cargarías tu lista desde el ViewModel en el futuro
    val listaPnc = listOf(
        PncProducto("SKU-102", "Producto A", "Daño en empaque", "19/07/2026"),
        PncProducto("SKU-884", "Producto B", "Fecha de caducidad vencida", "18/07/2026")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .padding(16.dp)
    ) {
        // Encabezado
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444))
            Spacer(Modifier.width(8.dp))
            Text("Productos No Conforme (PNC)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005691))
        }

        Spacer(Modifier.height(16.dp))

        // Listado de PNC como si fuera Inventario
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaPnc) { pnc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = pnc.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                        Text(text = "SKU: ${pnc.sku}", fontSize = 12.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Motivo: ${pnc.motivo}", fontSize = 13.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Medium)
                        Text(text = "Reportado el: ${pnc.fecha}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}