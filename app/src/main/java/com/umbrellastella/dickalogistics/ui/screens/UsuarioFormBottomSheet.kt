package com.umbrellastella.dickalogistics.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.umbrellastella.dickalogistics.data.models.UsuarioResponse

@Composable
fun UsuarioFormBottomSheet(
    usuario: UsuarioResponse?,
    onSave: (String, String, Int, String?, String?, Boolean) -> Unit, // Se agregó String? para email
    onDelete: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var nUsuario by remember { mutableStateOf(usuario?.nombreUsuario ?: "") }
    var numE by remember { mutableStateOf(usuario?.numEmpleado?.toString() ?: "") }
    var email by remember { mutableStateOf(usuario?.email ?: "") } // Nuevo campo
    var pass by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(usuario?.esActivo ?: true) }

    Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = nUsuario, onValueChange = { nUsuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = numE, onValueChange = { numE = it }, label = { Text("Num Emp") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Usuario Activo?")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = {
                onSave(nombre, nUsuario, numE.toIntOrNull() ?: 0, email.ifBlank { null }, pass.ifBlank { null }, isActive)
            }
        ) {
            Text("Guardar")
        }

        if (usuario != null) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDelete(usuario.id) }
            ) {
                Text("Eliminar Usuario", color = Color.Red)
            }
        }
    }
}