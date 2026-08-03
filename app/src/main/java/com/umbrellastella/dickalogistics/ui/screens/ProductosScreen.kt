package com.umbrellastella.dickalogistics.home

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.umbrellastella.dickalogistics.data.models.ProductoResponse
import com.umbrellastella.dickalogistics.ui.theme.DickaBlue
import com.umbrellastella.dickalogistics.ui.theme.DickaLightBackground
import com.umbrellastella.dickalogistics.viewmodel.ProductoUiState
import com.umbrellastella.dickalogistics.viewmodel.ProductoViewModel
import com.google.gson.annotations.SerializedName

data class ProductoFormState(
    val id: Int? = null,
    val sku: String = "",
    val nombre: String = "",

    @SerializedName("cliente_id")
    val clienteId: String = "",

    val propiedades: String = "",
    val caracteristicas: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(viewModel: ProductoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    var mostrarFormulario by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<ProductoResponse?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CATÁLOGO DE PRODUCTOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                    productoAEditar = null
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
                Text("Agregar Producto", color = Color.White, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = viewModel.textoBusqueda,
                onValueChange = { viewModel.actualizarBusqueda(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre, SKU o cliente...") },
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
                is ProductoUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = DickaBlue)
                    }
                }
                is ProductoUiState.Success -> {
                    if (viewModel.productosFiltrados.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron productos.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.productosFiltrados) { prod ->
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
                                                    text = prod.nombre,
                                                    color = DickaBlue,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(DickaLightBackground, RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = prod.cliente ?: "Sin Cliente",
                                                        color = DickaBlue,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    productoAEditar = prod
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

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "SKU: ${prod.sku}",
                                            color = DickaBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )

                                        if (!prod.caracteristicas.isNullOrEmpty()) {
                                            Text(
                                                text = "Características: ${prod.caracteristicas}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is ProductoUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.mensaje, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.cargarDatosIniciales() },
                            colors = ButtonDefaults.buttonColors(containerColor = DickaBlue)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }
        }

        if (mostrarFormulario) {
            FormularioProductoBottomSheet(
                productoAEditar = productoAEditar,
                clientesDisponibles = viewModel.listaClientes,
                onDismiss = { mostrarFormulario = false },
                onSave = { stateForm ->
                    mostrarFormulario = false
                    if (stateForm.id == null) {
                        viewModel.registrarProducto(stateForm) { exitoso ->
                            if (exitoso) {
                                Toast.makeText(context, "Producto registrado con éxito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al registrar producto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        viewModel.modificarProducto(stateForm.id, stateForm) { exitoso ->
                            if (exitoso) {
                                Toast.makeText(context, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar producto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                onDelete = { idEliminar ->
                    mostrarFormulario = false
                    viewModel.eliminarProducto(idEliminar) { exitoso ->
                        if (exitoso) {
                            Toast.makeText(context, "Producto eliminado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}