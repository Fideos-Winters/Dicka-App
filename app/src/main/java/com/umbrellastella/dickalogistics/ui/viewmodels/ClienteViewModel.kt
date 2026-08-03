package com.umbrellastella.dickalogistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.models.ClienteResponse
import com.umbrellastella.dickalogistics.data.repositories.ClienteRepository
import com.umbrellastella.dickalogistics.home.ClienteFormState
import kotlinx.coroutines.launch

sealed interface ClienteUiState {
    object Loading : ClienteUiState
    data class Success(val clientes: List<ClienteResponse>) : ClienteUiState
    data class Error(val mensaje: String) : ClienteUiState
}

class ClienteViewModel : ViewModel() {
    private val repository = ClienteRepository()

    var uiState: ClienteUiState by mutableStateOf(ClienteUiState.Loading)
    var listaClientes by mutableStateOf<List<ClienteResponse>>(emptyList())
    var clientesFiltrados by mutableStateOf<List<ClienteResponse>>(emptyList())
    var textoBusqueda by mutableStateOf("")

    init {
        cargarClientes()
    }

    fun cargarClientes() {
        viewModelScope.launch {
            uiState = ClienteUiState.Loading
            repository.obtenerClientes()
                .onSuccess { datos ->
                    listaClientes = datos
                    filtrarClientes()
                    uiState = ClienteUiState.Success(listaClientes)
                }
                .onFailure { error ->
                    uiState = ClienteUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun actualizarBusqueda(nuevaBusqueda: String) {
        textoBusqueda = nuevaBusqueda
        filtrarClientes()
    }

    private fun filtrarClientes() {
        clientesFiltrados = if (textoBusqueda.isBlank()) {
            listaClientes
        } else {
            listaClientes.filter {
                it.razonSocial.contains(textoBusqueda, ignoreCase = true) ||
                        it.domicilio.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    fun registrarCliente(cliente: ClienteFormState, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.registrarCliente(cliente)
                .onSuccess {
                    cargarClientes()
                    onResultado(true)
                }
                .onFailure { onResultado(false) }
        }
    }

    fun modificarCliente(id: Int, cliente: ClienteFormState, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.actualizarCliente(id, cliente)
                .onSuccess {
                    cargarClientes()
                    onResultado(true)
                }
                .onFailure { onResultado(false) }
        }
    }

    fun eliminarCliente(id: Int, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.eliminarCliente(id)
                .onSuccess {
                    cargarClientes()
                    onResultado(true)
                }
                .onFailure { onResultado(false) }
        }
    }
}