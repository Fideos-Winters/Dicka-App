package com.umbrellastella.dickalogistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.ProductoResponse
import com.umbrellastella.dickalogistics.data.RetrofitClient
import kotlinx.coroutines.launch

sealed interface ProductoUiState {
    object Loading : ProductoUiState
    object Success : ProductoUiState
    data class Error(val mensaje: String) : ProductoUiState
}

class ProductoViewModel : ViewModel() {

    var uiState: ProductoUiState by mutableStateOf(ProductoUiState.Loading)
        private set

    // Listas para control de filtros
    private var listaCompletaProductos = listOf<ProductoResponse>()
    var productosFiltrados by mutableStateOf(listOf<ProductoResponse>())
        private set

    // Estados de la barra de búsqueda
    var textoBusqueda by mutableStateOf("")
        private set

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            uiState = ProductoUiState.Loading
            try {
                val respuesta = RetrofitClient.apiService.obtenerProductos()
                listaCompletaProductos = respuesta
                productosFiltrados = respuesta
                uiState = ProductoUiState.Success
            } catch (e: Exception) {
                uiState = ProductoUiState.Error("Error al cargar inventario: ${e.localizedMessage}")
            }
        }
    }

    // Función lógica encargada de filtrar en tiempo real en memoria
    fun actualizarBusqueda(nuevoTexto: String) {
        textoBusqueda = nuevoTexto
        productosFiltrados = if (nuevoTexto.isBlank()) {
            listaCompletaProductos
        } else {
            listaCompletaProductos.filter { producto ->
                producto.nombre.contains(nuevoTexto, ignoreCase = true) ||
                        producto.sku.contains(nuevoTexto, ignoreCase = true) ||
                        producto.cliente.contains(nuevoTexto, ignoreCase = true) // Filtro por categoría/cliente
            }
        }
    }
}
