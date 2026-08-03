package com.umbrellastella.dickalogistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.models.DashboardResponse
import com.umbrellastella.dickalogistics.data.repositories.DashboardRepository
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val datos: DashboardResponse) : DashboardUiState
    data class Error(val mensaje: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {

    private val repository = DashboardRepository()

    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    init {
        cargarDatosDelDashboard()
    }

    fun cargarDatosDelDashboard() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            val resultado = repository.obtenerEstadisticasDashboard()

            resultado.onSuccess { datos ->
                uiState = DashboardUiState.Success(datos)
            }.onFailure { error ->
                uiState = DashboardUiState.Error("Error de conexión: ${error.localizedMessage}")
            }
        }
    }
}