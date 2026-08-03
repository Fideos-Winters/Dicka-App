package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbrellastella.dickalogistics.data.models.LoteSalidaModel
import com.umbrellastella.dickalogistics.data.models.QrLoteContainer
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.theme.DickaOrange

private val DickaWhite = Color(0xFFFFFFFF)
private val ColorBorder = Color(0xFFCBD5E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioSalidaBottomSheet(
    salidaAEditar: LoteSalidaModel?,
    onDismiss: () -> Unit,
    onSave: (LoteSalidaModel) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Estados de los campos basados en los requerimientos de Laravel
    var loteIdInput by remember { mutableStateOf(salidaAEditar?.loteIdDirecto?.toString() ?: salidaAEditar?.qrLote?.loteId?.toString() ?: "") }
    var palletNumeroInput by remember { mutableStateOf(salidaAEditar?.qrLote?.palletNumero?.toString() ?: "") }
    var qrEmbarqueInput by remember { mutableStateOf(salidaAEditar?.qrEmbarque ?: "") }
    var paletPisoInput by remember { mutableStateOf(salidaAEditar?.paletPiso?.toString() ?: "") }
    var cantidadEntregadaInput by remember { mutableStateOf(salidaAEditar?.cantidadEntregada?.toString() ?: "") }
    var observacionesInput by remember { mutableStateOf(salidaAEditar?.observaciones ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DickaWhite,
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
                text = if (salidaAEditar == null) "Registrar Nueva Salida" else "Editar Registro de Salida",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = loteIdInput,
                    onValueChange = { loteIdInput = it },
                    label = { Text("ID Lote") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = ColorBorder)
                )

                OutlinedTextField(
                    value = palletNumeroInput,
                    onValueChange = { palletNumeroInput = it },
                    label = { Text("N° Pallet") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = ColorBorder)
                )
            }

            OutlinedTextField(
                value = qrEmbarqueInput,
                onValueChange = { qrEmbarqueInput = it },
                label = { Text("QR Embarque / Documento") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = ColorBorder)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = paletPisoInput,
                    onValueChange = { paletPisoInput = it },
                    label = { Text("Pallet Piso") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = ColorBorder)
                )

                OutlinedTextField(
                    value = cantidadEntregadaInput,
                    onValueChange = { cantidadEntregadaInput = it },
                    label = { Text("Cantidad Uds") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = ColorBorder)
                )
            }

            OutlinedTextField(
                value = observacionesInput,
                onValueChange = { observacionesInput = it },
                label = { Text("Observaciones (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = ColorBorder)
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
                        val loteId = loteIdInput.toIntOrNull()
                        val palletNum = palletNumeroInput.toIntOrNull()
                        val paletPiso = paletPisoInput.toIntOrNull()
                        val cantidad = cantidadEntregadaInput.toIntOrNull()

                        if (loteId != null && palletNum != null && paletPiso != null && cantidad != null && qrEmbarqueInput.isNotBlank()) {
                            val salidaProcesada = LoteSalidaModel(
                                id = salidaAEditar?.id,
                                qrLote = QrLoteContainer(loteId = loteId, palletNumero = palletNum),
                                qrEmbarque = qrEmbarqueInput,
                                paletPiso = paletPiso,
                                cantidadEntregada = cantidad,
                                observaciones = observacionesInput.ifBlank { null }
                            )
                            onSave(salidaProcesada)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DickaOrange, contentColor = DickaWhite),
                    shape = RoundedCornerShape(12.dp),
                    enabled = loteIdInput.isNotBlank() && palletNumeroInput.isNotBlank() && qrEmbarqueInput.isNotBlank() && paletPisoInput.isNotBlank() && cantidadEntregadaInput.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}