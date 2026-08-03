package com.umbrellastella.dickalogistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.models.ClienteListadoItem
import com.umbrellastella.dickalogistics.data.models.ProductoResponse
import com.umbrellastella.dickalogistics.data.repositories.ProductoRepository
import com.umbrellastella.dickalogistics.home.ProductoFormState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

sealed interface ProductoUiState {
    object Loading : ProductoUiState
    object Success : ProductoUiState
    data class Error(val mensaje: String) : ProductoUiState
}

class ProductoViewModel : ViewModel() {
    private val repository = ProductoRepository()

    var uiState: ProductoUiState by mutableStateOf(ProductoUiState.Loading)
    var listaCompletaProductos by mutableStateOf(listOf<ProductoResponse>())
    var productosFiltrados by mutableStateOf(listOf<ProductoResponse>())
    var listaClientes by mutableStateOf(listOf<ClienteListadoItem>())
    var textoBusqueda by mutableStateOf("")

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        viewModelScope.launch {
            uiState = ProductoUiState.Loading

            // Refrescamos ambas listas para evitar inconsistencias
            val deferredClientes = async { repository.obtenerClientesListado() }
            val deferredProductos = async { repository.obtenerProductos() }

            val resClientes = deferredClientes.await()
            val resProductos = deferredProductos.await()

            resClientes.onSuccess { listaClientes = it }
            resProductos.onSuccess {
                listaCompletaProductos = it
                filtrarProductosLocales()
                uiState = ProductoUiState.Success
            }.onFailure {
                uiState = ProductoUiState.Error(it.message ?: "Error al refrescar datos")
            }
        }
    }

    fun registrarProducto(producto: ProductoFormState, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.registrarProducto(producto)
                .onSuccess {
                    cargarDatosIniciales()
                    onResult(true)
                }
                .onFailure { onResult(false) }
        }
    }

    fun modificarProducto(id: Int, producto: ProductoFormState, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.actualizarProducto(id, producto)
                .onSuccess {
                    cargarDatosIniciales()
                    onResult(true)
                }
                .onFailure { onResult(false) }
        }
    }

    fun eliminarProducto(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.eliminarProducto(id)
                .onSuccess {
                    cargarDatosIniciales()
                    onResult(true)
                }
                .onFailure { onResult(false) }
        }
    }

    fun actualizarBusqueda(nuevoTexto: String) {
        textoBusqueda = nuevoTexto
        filtrarProductosLocales()
    }

    private fun filtrarProductosLocales() {
        productosFiltrados = if (textoBusqueda.isBlank()) {
            listaCompletaProductos
        } else {
            listaCompletaProductos.filter {
                it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        (it.sku ?: "").contains(textoBusqueda, ignoreCase = true)
            }
        }
    }
}