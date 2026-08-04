package com.umbrellastella.dickalogistics.ui.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.models.LoteEntradaModel
import com.umbrellastella.dickalogistics.data.repositories.LoteRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.DickaApiService
import com.umbrellastella.dickalogistics.data.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
sealed interface LoteUiState {
    object Cargando : LoteUiState
    data class Exito(val lista: List<LoteEntradaModel>) : LoteUiState
    data class Error(val mensaje: String) : LoteUiState
}

class LoteViewModel : ViewModel() {
    private val repositorio = LoteRepository()

    var uiState: LoteUiState by mutableStateOf(LoteUiState.Cargando)
        private set

    init {
        cargarLotes()
    }

    fun cargarLotes() {
        uiState = LoteUiState.Cargando
        viewModelScope.launch {
            repositorio.obtenerLotes()
                .onSuccess { uiState = LoteUiState.Exito(it) }
                .onFailure { uiState = LoteUiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun registrarLote(lote: LoteEntradaModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repositorio.guardarLote(lote)
                .onSuccess {
                    onResult(true)
                    cargarLotes()
                }
                .onFailure {
                    onResult(false)
                }
        }
    }

    fun modificarLote(id: Int, lote: LoteEntradaModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repositorio.actualizarLote(id, lote)
                .onSuccess {
                    onResult(true)
                    cargarLotes()
                }
                .onFailure {
                    onResult(false)
                }
        }
    }
    private val _pdfEtiqueta = MutableLiveData<ResponseBody?>()
    val pdfEtiqueta: LiveData<ResponseBody?> get() = _pdfEtiqueta

    fun descargarEtiquetaParaImprimir(loteId: Int, onComplete: (ResponseBody?) -> Unit) {
        viewModelScope.launch {
            // Llamamos al repositorio
            val resultado = repositorio.descargarEtiquetaPdf(loteId)
            // Devolvemos el PDF (o null si falló) a la pantalla
            onComplete(resultado.getOrNull())
        }
    }
    fun recibirLoteEnRampa(loteId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {

                val response = RetrofitClient.apiService.recibirLote(loteId)

                if (response.isSuccessful) {
                    val body = response.body()
                    val mensaje = body?.get("message")?.asString ?: "Lote recibido con éxito"

                    // Recargamos la lista de lotes en la pantalla de entradas
                    cargarLotes()

                    onResult(true, mensaje)
                } else {
                    onResult(false, "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, "Excepción de red: ${e.localizedMessage ?: "Desconocido"}")
            }
        }
    }


}