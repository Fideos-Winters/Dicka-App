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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umbrellastella.dickalogistics.data.models.LoteEntradaModel
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.viewmodels.LoteUiState
import com.umbrellastella.dickalogistics.ui.viewmodels.LoteViewModel
import com.umbrellastella.dickalogistics.utils.PrintHelper
import com.umbrellastella.dickalogistics.viewmodel.ProductoViewModel

private val BackgroundGray = Color(0xFFF0F4F8)
private val DickaWhite = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradaLotesScreen(
    onBackToInventario: () -> Unit,
    viewModel: LoteViewModel = viewModel(),
    productoViewModel: ProductoViewModel = viewModel()

) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showPrintDialog by remember { mutableStateOf<LoteEntradaModel?>(null) }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var loteAEditar by remember { mutableStateOf<LoteEntradaModel?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    loteAEditar = null
                    mostrarFormulario = true
                },
                containerColor = DickaBlue,
                contentColor = DickaWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Lote", modifier = Modifier.size(28.dp))
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
                placeholder = { Text("Buscar por lote o SKU...", color = TextSecondary) },
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
                text = "Historial de Entradas Recientes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (val state = viewModel.uiState) {
                is LoteUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DickaBlue)
                    }
                }
                is LoteUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning, // Icono de advertencia
                            contentDescription = "Error de conexión",
                            tint = Color(0xFFEF4444), // Un rojo más amigable
                            modifier = Modifier.size(50.dp).padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Vaya, tuvimos un problema de conexión:\n${state.mensaje}",
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = { viewModel.cargarLotes() },
                            colors = ButtonDefaults.buttonColors(containerColor = DickaBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reintentar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reintentar Carga", fontWeight = FontWeight.Bold)
                        }
                    }
                }




                is LoteUiState.Exito -> {
                    val lotesFiltrados = state.lista.filter { lote ->
                        val coincideLote = lote.lote.contains(searchQuery, ignoreCase = true)
                        val coincideSku = lote.productoRelacion?.sku?.contains(searchQuery, ignoreCase = true) ?: false
                        coincideLote || coincideSku
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(lotesFiltrados) { lote ->
                            ItemLoteEntrada(
                                lote = lote,
                                onEditClick = {
                                    loteAEditar = lote
                                    mostrarFormulario = true
                                },
                                onLoteClick = { showPrintDialog = lote }
                            )
                        }
                    }
                }
            }
        }

        if (mostrarFormulario) {
            FormularioLoteBottomSheet(
                loteAEditar = loteAEditar,
                listaProductos = productoViewModel.listaCompletaProductos,
                onDismiss = { mostrarFormulario = false },
                onSave = { loteProcesado ->
                    mostrarFormulario = false
                    if (loteProcesado.id == null) {
                        viewModel.registrarLote(loteProcesado) { exito ->
                            val msg = if (exito) "Lote registrado con éxito" else "Fallo al registrar lote"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        viewModel.modificarLote(loteProcesado.id, loteProcesado) { exito ->
                            val msg = if (exito) "Lote actualizado con éxito" else "Fallo al actualizar lote"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        showPrintDialog?.let { lote ->
            AlertDialog(
                onDismissRequest = { showPrintDialog = null },
                confirmButton = {
                    TextButton(onClick = {
                        // 1. Verificamos que el lote tenga un ID válido
                        val loteId = lote.id
                        if (loteId != null) {
                            Toast.makeText(context, "Preparando etiqueta...", Toast.LENGTH_SHORT).show()

                            // 2. Llamamos al ViewModel con nuestro callback
                            viewModel.descargarEtiquetaParaImprimir(loteId) { responseBody ->
                                if (responseBody != null) {
                                    // 3. ¡Si hay PDF, lanzamos el PrintHelper!
                                    PrintHelper.guardarEImprimirPdf(context, responseBody, loteId)
                                } else {
                                    Toast.makeText(context, "Error al descargar la etiqueta. Verifica tu conexión.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Error: Lote sin ID válido", Toast.LENGTH_SHORT).show()
                        }

                        // 4. Cerramos el diálogo de inmediato mientras descarga en fondo
                        showPrintDialog = null
                    }) {
                        Text("Imprimir Etiqueta", color = DickaBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPrintDialog = null }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                },
                title = { Text("Orden de Impresión", color = DickaBlue, fontWeight = FontWeight.Bold) },
                text = { Text("¿Generar ticket para el lote ${lote.lote}?") }
            )
        }
    }
}

@Composable
fun ItemLoteEntrada(
    lote: LoteEntradaModel,
    onEditClick: () -> Unit,
    onLoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLoteClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DickaWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = lote.productoRelacion?.nombre ?: "Producto Desconocido", fontWeight = FontWeight.Bold, color = DickaBlue, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Folio: ${lote.folio}", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(text = "Lote: ${lote.lote}", fontSize = 12.sp, color = TextSecondary)
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
                    Text(text = "Estado", fontSize = 11.sp, color = TextSecondary)
                    Text(text = lote.ubi, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DickaBlue)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Caducidad", fontSize = 11.sp, color = TextSecondary)
                    Text(text = lote.caducidad, fontSize = 12.sp, color = Color(0xFF334155))
                }
            }
        }
    }
}