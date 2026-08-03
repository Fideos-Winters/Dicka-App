package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umbrellastella.dickalogistics.data.models.UsuarioResponse
import com.umbrellastella.dickalogistics.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(viewModel: UsuarioViewModel = viewModel()) {
    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioSeleccionado by remember { mutableStateOf<UsuarioResponse?>(null) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { usuarioSeleccionado = null; mostrarFormulario = true }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.listaUsuarios) { usuario ->
                    Card(elevation = CardDefaults.cardElevation(4.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.nombre, fontWeight = FontWeight.Bold)
                                Text("Usuario: ${usuario.nombreUsuario}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { usuarioSeleccionado = usuario; mostrarFormulario = true }) {
                                Icon(Icons.Default.Edit, null)
                            }
                        }
                    }
                }
            }

            if (viewModel.isCargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (mostrarFormulario) {
            ModalBottomSheet(onDismissRequest = { mostrarFormulario = false }) {
                var nombre by remember { mutableStateOf(usuarioSeleccionado?.nombre ?: "") }
                var nUsuario by remember { mutableStateOf(usuarioSeleccionado?.nombreUsuario ?: "") }
                var numE by remember { mutableStateOf(usuarioSeleccionado?.numEmpleado?.toString() ?: "") }
                var email by remember { mutableStateOf(usuarioSeleccionado?.email ?: "") }
                var pass by remember { mutableStateOf("") }
                var isActive by remember { mutableStateOf(usuarioSeleccionado?.esActivo ?: true) }

                Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = nUsuario, onValueChange = { nUsuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = numE, onValueChange = { numE = it }, label = { Text("Num Emp") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Usuario Activo?")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        enabled = !viewModel.isCargando,
                        onClick = {
                            // Ajusta los argumentos a lo que espera tu ViewModel
                            viewModel.guardarUsuario(
                                usuarioSeleccionado?.id, nombre, nUsuario, numE.toIntOrNull() ?: 0, email.ifBlank { null }, pass.ifBlank { null }, isActive
                            ) { exito ->
                                if (exito) mostrarFormulario = false
                                else scope.launch { snackbarHostState.showSnackbar("Error al guardar") }
                            }
                        }
                    ) { Text(if (viewModel.isCargando) "Procesando..." else "Guardar") }

                    if (usuarioSeleccionado != null) {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.eliminarUsuario(usuarioSeleccionado!!.id) { exito ->
                                    if (exito) mostrarFormulario = false
                                    else scope.launch { snackbarHostState.showSnackbar("Error al eliminar") }
                                }
                            }
                        ) { Text("Eliminar", color = Color.Red) }
                    }
                }
            }
        }
    }
}