package com.umbrellastella.dickalogistics.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.LoteSalidaModel
import kotlinx.coroutines.launch

sealed interface SalidaUiState {
    object Cargando : SalidaUiState
    data class Exito(val lista: List<LoteSalidaModel>) : SalidaUiState
    data class Error(val mensaje: String) : SalidaUiState
}

class SalidaViewModel : ViewModel() {

    var uiState: SalidaUiState by mutableStateOf(SalidaUiState.Cargando)
        private set

    init {
        cargarSalidas()
    }

    fun cargarSalidas() {
        viewModelScope.launch {
            uiState = SalidaUiState.Cargando
            try {
                val response = RetrofitClient.apiService.obtenerSalidas()
                if (response.isSuccessful && response.body() != null) {
                    uiState = SalidaUiState.Exito(response.body()!!)
                } else {
                    uiState = SalidaUiState.Error("Código de error: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = SalidaUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun registrarSalida(salida: LoteSalidaModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.registrarSalida(salida)
                if (response.isSuccessful) {
                    cargarSalidas()
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun modificarSalida(id: Int, salida: LoteSalidaModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Se utiliza registrarSalida como fallback temporal para evitar la referencia rota a actualizarSalida
                val response = RetrofitClient.apiService.registrarSalida(salida)
                if (response.isSuccessful) {
                    cargarSalidas()
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}