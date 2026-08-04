package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.data.models.LoteEntradaModel
import com.umbrellastella.dickalogistics.data.models.ProductoResponse
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue

private val DickaWhite = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioLoteBottomSheet(
    loteAEditar: LoteEntradaModel? = null,
    listaProductos: List<ProductoResponse> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (LoteEntradaModel) -> Unit
) {
    var folio by remember { mutableStateOf(loteAEditar?.folio?.toString() ?: "") }

    // Variables de Estado para el Selector de Producto
    var selectedProductoId by remember { mutableStateOf(loteAEditar?.productoId) }
    var selectedProductoNombre by remember {
        mutableStateOf(loteAEditar?.productoRelacion?.nombre ?: "")
    }
    var mostrarBuscadorProductos by remember { mutableStateOf(false) }

    var loteCodigo by remember { mutableStateOf(loteAEditar?.lote ?: "") }
    var caducidad by remember { mutableStateOf(loteAEditar?.caducidad ?: "") }
    var numPalets by remember { mutableStateOf(loteAEditar?.numPalets?.toString() ?: "") }
    var piezasPalet by remember { mutableStateOf(loteAEditar?.piezasPalet?.toString() ?: "") }
    var unidadMedida by remember { mutableStateOf(loteAEditar?.unidadMedida ?: "Pzas") }
    var observaciones by remember { mutableStateOf(loteAEditar?.observaciones ?: "") }
    var operador by remember { mutableStateOf(loteAEditar?.operador ?: "") }
    var lt by remember { mutableStateOf(loteAEditar?.lt ?: "") }
    var placas by remember { mutableStateOf(loteAEditar?.placas ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DickaWhite,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFCBD5E1)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (loteAEditar == null) "Crear Nuevo Lote" else "Modificar Lote: ${loteAEditar.lote}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue
            )

            HorizontalDivider(color = Color(0xFFE2E8F0))

            // 1. Folio
            OutlinedTextField(
                value = folio,
                onValueChange = { folio = it },
                label = { Text("Folio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Selector de Producto (Corregido y cerrado de forma limpia)
            OutlinedTextField(
                value = if (selectedProductoNombre.isNotEmpty()) selectedProductoNombre else "Seleccionar Producto...",
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Producto Seleccionado", color = DickaBlue) },
                trailingIcon = {
                    Icon(
                        imageVector = if (mostrarBuscadorProductos) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar Catálogo",
                        tint = DickaBlue
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = if (selectedProductoId != null) DickaBlue else Color(0xFFCBD5E1),
                    disabledTextColor = if (selectedProductoId != null) Color.Black else TextSecondary,
                    disabledLabelColor = DickaBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarBuscadorProductos = true }
            )

            // 3. Código Lote
            OutlinedTextField(
                value = loteCodigo,
                onValueChange = { loteCodigo = it },
                label = { Text("Código Lote") },
                modifier = Modifier.fillMaxWidth()
            )

            // 4. Fecha Caducidad
            OutlinedTextField(
                value = caducidad,
                onValueChange = { caducidad = it },
                label = { Text("Fecha Caducidad (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Pallets y Piezas (En fila)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = numPalets,
                    onValueChange = { numPalets = it },
                    label = { Text("Cantidad Pallets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = piezasPalet,
                    onValueChange = { piezasPalet = it },
                    label = { Text("Pzas x Pallet") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // 6. Operador (Campo Individual)
            OutlinedTextField(
                value = operador,
                onValueChange = { operador = it },
                label = { Text("Operador") },
                modifier = Modifier.fillMaxWidth()
            )

            // 7. LT y Placas (En fila)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = lt,
                    onValueChange = { lt = it },
                    label = { Text("LT") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = placas,
                    onValueChange = { placas = it },
                    label = { Text("Placas") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 8. Unidad de Medida
            OutlinedTextField(
                value = unidadMedida,
                onValueChange = { unidadMedida = it },
                label = { Text("Unidad de Medida") },
                modifier = Modifier.fillMaxWidth()
            )

            // 9. Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones del Operador") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 10. Botón de Guardar
            Button(
                onClick = {
                    if (selectedProductoId != null) {
                        onSave(
                            LoteEntradaModel(
                                id = loteAEditar?.id,
                                folio = folio.toIntOrNull() ?: 0,
                                productoId = selectedProductoId ?: 0,
                                lote = loteCodigo,
                                caducidad = caducidad,
                                fechaRecibido = loteAEditar?.fechaRecibido ?: "2026-07-20",
                                numPalets = numPalets.toIntOrNull() ?: 0,
                                piezasPalet = piezasPalet.toIntOrNull() ?: 0,
                                unidadMedida = unidadMedida,
                                operador = operador,
                                lt = lt,
                                placas = placas,
                                observaciones = observaciones.ifBlank { null },
                                ubi = loteAEditar?.ubi ?: "No Ubicado"
                            )
                        )
                    }
                },
                enabled = selectedProductoId != null,
                colors = ButtonDefaults.buttonColors(containerColor = DickaBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (loteAEditar == null) "Guardar Lote" else "Actualizar Cambios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DickaWhite
                )
            }
        }
    }

    // --- 🪟 SUB-MENÚ FLOTANTE CON BUSCADOR DE PRODUCTOS ---
    if (mostrarBuscadorProductos) {
        DialogBuscadorProductos(
            listaProductos = listaProductos,
            onDismiss = { mostrarBuscadorProductos = false },
            onProductoSelected = { producto ->
                selectedProductoId = producto.id
                selectedProductoNombre = "${producto.nombre} (${producto.sku ?: "Sin SKU"})"
                mostrarBuscadorProductos = false
            }
        )
    }
}

@Composable
fun DialogBuscadorProductos(
    listaProductos: List<ProductoResponse>,
    onDismiss: () -> Unit,
    onProductoSelected: (ProductoResponse) -> Unit
) {
    var filtroBusqueda by remember { mutableStateOf("") }

    val productosFiltrados = remember(filtroBusqueda, listaProductos) {
        if (filtroBusqueda.isBlank()) {
            listaProductos
        } else {
            listaProductos.filter {
                it.nombre.contains(filtroBusqueda, ignoreCase = true) ||
                        (it.sku ?: "").contains(filtroBusqueda, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Buscar Producto", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DickaBlue)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                OutlinedTextField(
                    value = filtroBusqueda,
                    onValueChange = { filtroBusqueda = it },
                    placeholder = { Text("Escribe nombre o SKU...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DickaBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                if (productosFiltrados.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron productos", fontSize = 13.sp, color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(productosFiltrados) { producto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onProductoSelected(producto) },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = producto.nombre,
                                        fontWeight = FontWeight.Bold,
                                        color = DickaBlue,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "SKU: ${producto.sku ?: "Sin SKU"}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}