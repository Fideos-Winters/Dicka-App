package com.umbrellastella.dickalogistics.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umbrellastella.dickalogistics.data.models.LoteSalidaModel
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.viewmodels.SalidaUiState
import com.umbrellastella.dickalogistics.ui.viewmodels.SalidaViewModel

private val BackgroundGray = Color(0xFFF0F4F8)
private val DickaWhite = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalidaLotesScreen(
    onBackToInventario: () -> Unit,
    viewModel: SalidaViewModel = viewModel()
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showDetailDialog by remember { mutableStateOf<LoteSalidaModel?>(null) }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var salidaAEditar by remember { mutableStateOf<LoteSalidaModel?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    salidaAEditar = null
                    mostrarFormulario = true
                },
                containerColor = DickaBlue,
                contentColor = DickaWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Salida", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar por referencia...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DickaBlue) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DickaWhite,
                    unfocusedContainerColor = DickaWhite,
                    focusedBorderColor = DickaBlue,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                )
            )

            Text(
                text = "Historial de Salidas (Selecciona para editar)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (val state = viewModel.uiState) {
                is SalidaUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DickaBlue)
                    }
                }
                is SalidaUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${state.mensaje}", color = Color.Red)
                    }
                }
                is SalidaUiState.Exito -> {
                    // Corrección: Leer qrEmbarque y loteIdDirecto del modelo corregido
                    val salidasFiltradas = state.lista.filter { salida ->
                        val coincideRef = salida.qrEmbarque.contains(searchQuery, ignoreCase = true)
                        val coincideLoteId = (salida.loteIdDirecto?.toString() ?: "").contains(searchQuery)
                        coincideRef || coincideLoteId
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(salidasFiltradas) { salida ->
                            ItemLoteSalida(
                                salida = salida,
                                onEditClick = {
                                    salidaAEditar = salida
                                    mostrarFormulario = true
                                },
                                onSalidaClick = { showDetailDialog = salida }
                            )
                        }
                    }
                }
            }
        }

        if (mostrarFormulario) {
            FormularioSalidaBottomSheet(
                salidaAEditar = salidaAEditar,
                onDismiss = { mostrarFormulario = false },
                onSave = { salidaProcesada ->
                    mostrarFormulario = false
                    if (salidaProcesada.id == null) {
                        viewModel.registrarSalida(salidaProcesada) { exito ->
                            val msg = if (exito) "Salida registrada con éxito" else "Fallo al registrar salida"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        viewModel.modificarSalida(salidaProcesada.id, salidaProcesada) { exito ->
                            val msg = if (exito) "Salida actualizada con éxito" else "Fallo al actualizar salida"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        showDetailDialog?.let { salida ->
            AlertDialog(
                onDismissRequest = { showDetailDialog = null },
                confirmButton = {
                    TextButton(onClick = { showDetailDialog = null }) {
                        Text("Aceptar", color = DickaBlue, fontWeight = FontWeight.Bold)
                    }
                },
                title = { Text("Detalle de Salida", color = DickaBlue, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        // Corrección: Leer parámetros del modelo corregido
                        Text("Ref: ${salida.qrEmbarque}")
                        Text("ID Lote: ${salida.loteIdDirecto ?: 0}")
                        Text("Pallet Piso: ${salida.paletPiso}")
                        Text("Cantidad: ${salida.cantidadEntregada} uds")
                        salida.observaciones?.let { Text("Notas: $it") }
                    }
                }
            )
        }
    }
}

@Composable
fun ItemLoteSalida(
    salida: LoteSalidaModel,
    onEditClick: () -> Unit,
    onSalidaClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSalidaClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DickaWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    // Corrección: Pintar qrEmbarque
                    Text(text = "Ref: ${salida.qrEmbarque}", fontWeight = FontWeight.Bold, color = DickaBlue, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Lote ID: ${salida.loteIdDirecto ?: 0}", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = DickaBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", color = DickaBlue, fontSize = 12.sp)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    // Corrección: Pintar cantidadEntregada
                    Text(text = "Cantidad", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "-${salida.cantidadEntregada} uds", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Observaciones", fontSize = 11.sp, color = TextSecondary)
                    Text(text = salida.observaciones ?: "Sin notas", fontSize = 12.sp, color = Color(0xFF334155))
                }
            }
        }
    }
}