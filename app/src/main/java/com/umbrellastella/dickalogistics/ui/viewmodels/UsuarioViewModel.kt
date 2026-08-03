package com.umbrellastella.dickalogistics.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.models.*
import com.umbrellastella.dickalogistics.data.repositories.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {
    private val repository = UsuarioRepository()

    var listaUsuarios by mutableStateOf<List<UsuarioResponse>>(emptyList())
        private set

    var isCargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        isCargando = true
        viewModelScope.launch {
            Log.d("DEBUG_PROCESS", "Iniciando carga de usuarios desde repositorio...")
            repository.obtenerUsuarios()
                .onSuccess {
                    Log.d("DEBUG_PROCESS", "Usuarios recibidos: ${it.size}")
                    listaUsuarios = it
                    isCargando = false
                }
                .onFailure {
                    Log.e("DEBUG_PROCESS", "Error al cargar: ${it.message}")
                    mensajeError = "Error carga: ${it.message}"
                    isCargando = false
                }
        }
    }

    // Firma actualizada para incluir email e isActive
    fun guardarUsuario(
        id: Int?,
        nombre: String,
        nombreUsuario: String,
        numEmpleado: Int,
        email: String?,
        pass: String?,
        isActive: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        val request = UsuarioRequest(
            nombre = nombre,
            nombreUsuario = nombreUsuario,
            numEmpleado = numEmpleado,
            email = email,
            password = pass,
            IsActive = isActive,
            rol_id = 2
        )

        Log.d("DEBUG_PROCESS", "Guardando usuario: ${request.nombreUsuario}, Email: $email, Activo: $isActive")

        isCargando = true
        viewModelScope.launch {
            val res = if (id == null) repository.registrarUsuario(request) else repository.actualizarUsuario(id, request)

            res.onSuccess {
                Log.d("DEBUG_PROCESS", "Guardado exitoso")
                cargarUsuarios()
                onResult(true)
            }.onFailure {
                Log.e("DEBUG_PROCESS", "Fallo al guardar: ${it.message}")
                mensajeError = "Fallo al guardar: ${it.message}"
                onResult(false)
            }
            isCargando = false
        }
    }

    fun eliminarUsuario(id: Int, onResult: (Boolean) -> Unit) {
        Log.d("DEBUG_PROCESS", "Eliminando ID: $id")
        isCargando = true
        viewModelScope.launch {
            repository.eliminarUsuario(id)
                .onSuccess {
                    Log.d("DEBUG_PROCESS", "Eliminación exitosa")
                    cargarUsuarios()
                    onResult(true)
                }
                .onFailure {
                    Log.e("DEBUG_PROCESS", "Fallo al eliminar: ${it.message}")
                    onResult(false)
                }
            isCargando = false
        }
    }
}