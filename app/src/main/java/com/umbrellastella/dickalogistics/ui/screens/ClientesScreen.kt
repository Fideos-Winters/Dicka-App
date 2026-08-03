package com.umbrellastella.dickalogistics.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.google.gson.annotations.SerializedName
import com.umbrellastella.dickalogistics.data.models.ClienteResponse
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.theme.DickaLightBackground
import com.umbrellastella.dickalogistics.viewmodel.ClienteUiState
import com.umbrellastella.dickalogistics.viewmodel.ClienteViewModel

data class ClienteFormState(
    val id: Int? = null,
    val razonSocial: String = "",
    val domicilio: String = "",
    @SerializedName("usuario_id") val usuarioId: Int = 1 // Ajustar según el ID de sesión activa del login
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(viewModel: ClienteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    var mostrarFormulario by remember { mutableStateOf(false) }
    var clienteAEditar by remember { mutableStateOf<ClienteResponse?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CATÁLOGO DE CLIENTES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DickaBlue)
            )
        },
        containerColor = DickaLightBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    clienteAEditar = null
                    mostrarFormulario = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DickaBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Cliente", color = Color.White, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = viewModel.textoBusqueda,
                onValueChange = { viewModel.actualizarBusqueda(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por razón social o domicilio...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = DickaBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DickaBlue,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = DickaBlue
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = viewModel.uiState) {
                is ClienteUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DickaBlue)
                    }
                }
                is ClienteUiState.Success -> {
                    if (viewModel.clientesFiltrados.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron clientes.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.clientesFiltrados) { cte ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = cte.razonSocial,
                                                    color = DickaBlue,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Domicilio: ${cte.domicilio}",
                                                    color = Color.DarkGray,
                                                    fontSize = 13.sp
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    clienteAEditar = cte
                                                    mostrarFormulario = true
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Editar",
                                                    tint = DickaBlue,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is ClienteUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.mensaje, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.cargarClientes() },
                            colors = ButtonDefaults.buttonColors(containerColor = DickaBlue)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }
        }

        if (mostrarFormulario) {
            FormularioClienteBottomSheet(
                clienteAEditar = clienteAEditar,
                onDismiss = { mostrarFormulario = false },
                onSave = { stateForm ->
                    mostrarFormulario = false
                    if (stateForm.id == null) {
                        viewModel.registrarCliente(stateForm) { exitoso ->
                            if (exitoso) {
                                Toast.makeText(context, "Cliente registrado con éxito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al registrar cliente", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        viewModel.modificarCliente(stateForm.id, stateForm) { exitoso ->
                            if (exitoso) {
                                Toast.makeText(context, "Cliente actualizado con éxito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar cliente", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                onDelete = { idEliminar ->
                    mostrarFormulario = false
                    viewModel.eliminarCliente(idEliminar) { exitoso ->
                        if (exitoso) {
                            Toast.makeText(context, "Cliente eliminado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al eliminar cliente", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioClienteBottomSheet(
    clienteAEditar: ClienteResponse?,
    onDismiss: () -> Unit,
    onSave: (ClienteFormState) -> Unit,
    onDelete: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var razonSocialInput by remember { mutableStateOf(clienteAEditar?.razonSocial ?: "") }
    var domicilioInput by remember { mutableStateOf(clienteAEditar?.domicilio ?: "") }

    val isFormValid = razonSocialInput.isNotBlank() && domicilioInput.isNotBlank()

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
                text = if (clienteAEditar == null) "Registrar Nuevo Cliente" else "Editar Registro de Cliente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DickaBlue
            )

            OutlinedTextField(
                value = razonSocialInput,
                onValueChange = { razonSocialInput = it },
                label = { Text("Razón Social") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DickaBlue, unfocusedBorderColor = Color.LightGray)
            )

            OutlinedTextField(
                value = domicilioInput,
                onValueChange = { domicilioInput = it },
                label = { Text("Domicilio Operativo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
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
                                ClienteFormState(
                                    id = clienteAEditar?.id,
                                    razonSocial = razonSocialInput,
                                    domicilio = domicilioInput
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

            if (clienteAEditar?.id != null) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                Button(
                    onClick = { onDelete(clienteAEditar.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Cliente", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}