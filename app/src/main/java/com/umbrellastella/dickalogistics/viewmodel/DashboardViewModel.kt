package com.umbrellastella.dickalogistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.DashboardResponse
import com.umbrellastella.dickalogistics.data.RetrofitClient
import kotlinx.coroutines.launch

// Definimos los estados posibles de la pantalla de inicio
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val datos: DashboardResponse) : DashboardUiState
    data class Error(val mensaje: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {

    // Este es el estado que la vista va a estar observando
    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    init {
        cargarDatosDelDashboard()
    }

    fun cargarDatosDelDashboard() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            try {
                // Llamamos a la API usando el cliente de Retrofit
                val respuesta = RetrofitClient.apiService.obtenerEstadisticas()
                uiState = DashboardUiState.Success(respuesta)
            } catch (e: Exception) {
                uiState = DashboardUiState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}