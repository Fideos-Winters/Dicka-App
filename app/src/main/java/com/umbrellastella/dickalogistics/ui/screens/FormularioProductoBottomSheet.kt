package com.umbrellastella.dickalogistics.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.data.models.ClienteListadoItem
import com.umbrellastella.dickalogistics.data.models.ProductoResponse
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoBottomSheet(
    productoAEditar: ProductoResponse?,
    clientesDisponibles: List<ClienteListadoItem>,
    onDismiss: () -> Unit,
    onSave: (ProductoFormState) -> Unit,
    onDelete: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var skuInput by remember { mutableStateOf(productoAEditar?.sku ?: "") }
    var nombreInput by remember { mutableStateOf(productoAEditar?.nombre ?: "") }
    var propiedadesInput by remember { mutableStateOf(productoAEditar?.propiedades ?: "") }
    var caracteristicasInput by remember { mutableStateOf(productoAEditar?.caracteristicas ?: "") }

    var dropdownExpandido by remember { mutableStateOf(false) }

    // CORREGIDO: Buscamos únicamente por match de string para evitar el error de 'clientId' no existente
    var clienteSeleccionado by remember {
        mutableStateOf(
            clientesDisponibles.find {
                it.razonSocial.equals(productoAEditar?.cliente, ignoreCase = true)
            }
        )
    }

    val isFormValid = skuInput.isNotBlank() && nombreInput.isNotBlank() && clienteSeleccionado != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (productoAEditar == null) "Registrar Nuevo Producto" else "Editar Registro de Producto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue
            )

            OutlinedTextField(
                value = skuInput,
                onValueChange = { skuInput = it },
                label = { Text("SKU del Producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = productoAEditar == null,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = Color.LightGray)
            )

            OutlinedTextField(
                value = nombreInput,
                onValueChange = { nombreInput = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = Color.LightGray)
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpandido,
                onExpandedChange = { dropdownExpandido = !dropdownExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = clienteSeleccionado?.razonSocial ?: "Seleccionar Cliente",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("ID Cliente Asociado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DickaBlue,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpandido,
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    clientesDisponibles.forEach { itemCliente ->
                        DropdownMenuItem(
                            text = { Text(text = itemCliente.razonSocial) },
                            onClick = {
                                clienteSeleccionado = itemCliente
                                dropdownExpandido = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            OutlinedTextField(
                value = propiedadesInput,
                onValueChange = { propiedadesInput = it },
                label = { Text("Propiedades (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = Color.LightGray)
            )

            OutlinedTextField(
                value = caracteristicasInput,
                onValueChange = { caracteristicasInput = it },
                label = { Text("Características (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DickaBlue)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (isFormValid) {
                            onSave(
                                ProductoFormState(
                                    id = productoAEditar?.id,
                                    sku = skuInput,
                                    nombre = nombreInput,
                                    clienteId = clienteSeleccionado!!.id.toString(),
                                    propiedades = propiedadesInput,
                                    caracteristicas = caracteristicasInput
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DickaBlue, disabledContainerColor = Color.LightGray),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("Guardar")
                }
            }

            if (productoAEditar?.id != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                Button(
                    onClick = { onDelete(productoAEditar.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Producto", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}