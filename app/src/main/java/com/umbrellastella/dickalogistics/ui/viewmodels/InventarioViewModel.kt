package com.umbrellastella.dickalogistics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.repositories.InventarioRepository
import com.umbrellastella.dickalogistics.data.models.ProductoModel
import com.umbrellastella.dickalogistics.data.models.LoteDetalleModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {

    private val repository = InventarioRepository()

    // --- ESTADO PARA LA PANTALLA PRINCIPAL DE INVENTARIO ---
    private val _uiState = MutableStateFlow<InventarioUiState>(InventarioUiState.Cargando)
    val uiState: StateFlow<InventarioUiState> = _uiState.asStateFlow()

    // --- ESTADOS PARA LA PANTALLA DE DESGLOSE DE LOTES ---
    private val _lotesProducto = MutableStateFlow<List<LoteDetalleModel>>(emptyList())
    val lotesProducto: StateFlow<List<LoteDetalleModel>> = _lotesProducto.asStateFlow()

    private val _cargandoLotes = MutableStateFlow(false)
    val cargandoLotes: StateFlow<Boolean> = _cargandoLotes.asStateFlow()

    private val _errorLotes = MutableStateFlow<String?>(null)
    val errorLotes: StateFlow<String?> = _errorLotes.asStateFlow()

    init {
        cargarInventario()
    }

    /**
     * Carga el inventario general
     */
    fun cargarInventario() {
        viewModelScope.launch {
            _uiState.value = InventarioUiState.Cargando
            repository.getInventario()
                .onSuccess { productos ->
                    _uiState.value = InventarioUiState.Exito(productos)
                }
                .onFailure { excepcion ->
                    _uiState.value = InventarioUiState.Error(excepcion.message ?: "Error desconocido")
                }
        }
    }

    /**
     * Carga los lotes por SKU
     */
    fun cargarLotesPorProducto(sku: String) {
        viewModelScope.launch {
            _cargandoLotes.value = true
            _errorLotes.value = null
            repository.getLotesPorProducto(sku)
                .onSuccess { listaDeLotes ->
                    _lotesProducto.value = listaDeLotes
                    _cargandoLotes.value = false
                }
                .onFailure { excepcion ->
                    _errorLotes.value = excepcion.message ?: "Error al cargar lotes"
                    _cargandoLotes.value = false
                }
        }
    }
}

// --- AQUÍ ESTÁ EL INTERFAZ QUE FALTABA PARA QUE COMIENZE A COMPILAR LA VISTA ---
sealed interface InventarioUiState {
    object Cargando : InventarioUiState
    data class Exito(val productos: List<ProductoModel>) : InventarioUiState
    data class Error(val mensaje: String) : InventarioUiState
}