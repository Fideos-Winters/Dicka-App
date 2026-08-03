package com.umbrellastella.dickalogistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.models.RackResponse
import com.umbrellastella.dickalogistics.data.repository.RackRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapaRackViewModel : ViewModel() {
    private val repo = RackRepository()

    private val _mapaCompleto = MutableStateFlow<List<RackResponse>>(emptyList())
    val mapaCompleto: StateFlow<List<RackResponse>> = _mapaCompleto.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 🟢 Nuevo estado para avisarle a la UI si de plano falló todo y mostrar el botón
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    init {
        cargarMapaCompleto()
    }

    /**
     * Reintenta automáticamente hasta 3 veces si el JSON llega cortado.
     * Si falla tras los intentos, activa el estado de error para el botón manual.
     */
    fun cargarMapaCompleto(maxIntentos: Int = 3) {
        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false
            var intentoActual = 0
            var exito = false

            while (intentoActual < maxIntentos && !exito) {
                intentoActual++
                android.util.Log.d("FIRE_KEEPER", "🔥 Intento $intentoActual de $maxIntentos para invocar el mapa...")

                try {
                    val resultado = repo.getMapaCompleto()

                    // Si la lista regresó con datos, consideramos que el ritual triunfó
                    if (resultado.isNotEmpty()) {
                        _mapaCompleto.value = resultado
                        exito = true
                        android.util.Log.d("FIRE_KEEPER", "✨ Triunfo en el intento $intentoActual. Racks: ${resultado.size}")
                    } else if (intentoActual == maxIntentos) {
                        _isError.value = true
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FIRE_KEEPER", "💀 Maldición en intento $intentoActual: ${e.message}")

                    if (intentoActual >= maxIntentos) {
                        _mapaCompleto.value = emptyList()
                        _isError.value = true
                    } else {
                        // Esperamos 1 segundo antes de volver a atacar al servidor
                        delay(1000)
                    }
                }
            }
            _isLoading.value = false
        }
    }
}