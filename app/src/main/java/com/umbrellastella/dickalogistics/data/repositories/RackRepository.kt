package com.umbrellastella.dickalogistics.data.repository

import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.RackResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RackRepository {
    private val api = RetrofitClient.apiService

    suspend fun getMapaCompleto(): List<RackResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMapaCompleto()
            if (response.isSuccessful) {
                val cuerpo = response.body() ?: emptyList()
                android.util.Log.d("Pipzas", " Racks extraídos con éxito: ${cuerpo.size}")
                cuerpo
            } else {
                android.util.Log.e("Pipzas", "Error del servidor al llamar mapa-completo: ${response.code()} - ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("Pipzas", "Error al deserializar JSON de racks: ${e.message}", e)
            emptyList()
        }
    }
}