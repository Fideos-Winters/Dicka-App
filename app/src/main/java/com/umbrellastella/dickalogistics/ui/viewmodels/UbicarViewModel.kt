package com.umbrellastella.dickalogistics.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.LoteDetalleModel
import com.umbrellastella.dickalogistics.data.models.LoteUbicacionModel
import com.umbrellastella.dickalogistics.data.repositories.UbicarRepository
import kotlinx.coroutines.launch

class UbicarViewModel : ViewModel() {
    private val repository = UbicarRepository()

    // Listas separadas para cada pestaña
    var listaUbicados by mutableStateOf<List<LoteUbicacionModel>>(emptyList())
        private set

    var listaNoUbicados by mutableStateOf<List<LoteUbicacionModel>>(emptyList())
        private set

    var listaUbicacionesDisponibles by mutableStateOf<List<String>>(emptyList())
        private set

    // Contadores globales que manda Laravel
    var totalUbicados by mutableStateOf(0)
        private set
    var totalNoUbicados by mutableStateOf(0)
        private set

    // Estados de carga
    var isLoading by mutableStateOf(false)
        private set

    var isLoadingRecomendaciones by mutableStateOf(false)
        private set

    // Pestaña actual seleccionada: "ubicados" o "no_ubicados"
    var pestañaActual by mutableStateOf("ubicados")
        private set

    private var currentPageUbicados = 1
    private var currentPageNoUbicados = 1

    fun cambiarPestaña(nuevaPestaña: String) {
        pestañaActual = nuevaPestaña
        // Si la lista de la pestaña elegida está vacía, la cargamos
        if ((pestañaActual == "ubicados" && listaUbicados.isEmpty()) ||
            (pestañaActual == "no_ubicados" && listaNoUbicados.isEmpty())) {
            cargarLotes(reset = true)
        }
    }

    fun cargarLotes(loteQuery: String? = null, folioQuery: Int? = null, reset: Boolean = false) {
        viewModelScope.launch {
            if (isLoading) return@launch
            isLoading = true

            if (reset) {
                if (pestañaActual == "ubicados") currentPageUbicados = 1 else currentPageNoUbicados = 1
            }

            val page = if (pestañaActual == "ubicados") currentPageUbicados else currentPageNoUbicados

            try {
                val resultado = repository.filtrarLotes(loteQuery, folioQuery, page, pestañaActual)

                resultado?.let {
                    totalUbicados = it.total_ubicados
                    totalNoUbicados = it.total_no_ubicados

                    if (pestañaActual == "ubicados") {
                        listaUbicados = if (page == 1) it.data else listaUbicados + it.data
                    } else {
                        listaNoUbicados = if (page == 1) it.data else listaNoUbicados + it.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DICKA_LOG", "ERROR AL BUSCAR LOTES: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun cargarMas() {
        if (pestañaActual == "ubicados") {
            currentPageUbicados++
        } else {
            currentPageNoUbicados++
        }
        cargarLotes(reset = false)
    }

    fun estaUbicado(lote: LoteUbicacionModel): Boolean {
        val qr = lote.qrUbicacion?.trim() ?: ""
        return qr.isNotEmpty() &&
                !qr.equals("SIN UBICAR", ignoreCase = true) &&
                !qr.equals("No Ubicado", ignoreCase = true) &&
                !qr.equals("null", ignoreCase = true)
    }

    fun cargarRecomendaciones(loteId: Int) {
        viewModelScope.launch {
            isLoadingRecomendaciones = true
            try {
                val recomendadas = repository.obtenerUbicacionesDisponibles(loteId)
                listaUbicacionesDisponibles = recomendadas
            } catch (e: Exception) {
                listaUbicacionesDisponibles = emptyList()
            } finally {
                isLoadingRecomendaciones = false
            }
        }
    }

    fun asignarUbicacion(lote: LoteUbicacionModel, ubicacion: String) {
        viewModelScope.launch {
            val id = lote.loteId ?: lote.id
            val pallet = lote.palletNumero ?: 1
            val cantidad = 1

            if (id > 0) {
                isLoading = true
                val exito = repository.asignarUbicacion(
                    loteId = id,
                    palletNumero = pallet,
                    cantidad = cantidad,
                    ubicacion = ubicacion
                )

                if (exito) {
                    listaUbicacionesDisponibles = emptyList()
                    // Recargamos la pestaña actual desde la página 1 para reflejar los cambios
                    cargarLotes(reset = true)
                }
                isLoading = false
            }
        }
    }

    fun verificarEstatusLote(loteId: Int, onResultado: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.detalleLote(loteId)

                if (response.isSuccessful && response.body() != null) {
                    val detalle = response.body()!!

                    // 🟢 AQUÍ ESTÁ EL TRUCO:
                    // Imprimimos en el Logcat qué estatus exacto nos mandó Laravel para este ID
                    val estadoServidor = detalle.ubicacion ?: "Pendiente"
                    android.util.Log.d("ESTATUS_API", "🔍 Estado real devuelto por Laravel para ID $loteId: $estadoServidor")

                    onResultado(estadoServidor)
                } else {
                    onResultado("Pendiente")
                }
            } catch (e: Exception) {
                onResultado("Pendiente")
            }
        }
    }
}