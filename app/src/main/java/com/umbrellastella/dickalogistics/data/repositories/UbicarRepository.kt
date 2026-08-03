package com.umbrellastella.dickalogistics.data.repositories

import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.LoteRespuestaModel
import com.umbrellastella.dickalogistics.data.models.LoteUbicacionModel
import com.umbrellastella.dickalogistics.data.models.QrLoteData
import com.umbrellastella.dickalogistics.data.models.RegistrarUbicacionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UbicarRepository {

    suspend fun filtrarLotes(lote: String?, folio: Int?, page: Int, estado: String): LoteRespuestaModel? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService.filtrarLotes(lote, folio, page, estado)
            if (response.isSuccessful) {
                response.body() // Regresamos todo el modelo con los contadores y la lista
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("DICKA_LOG", "ERROR AL BUSCAR LOTES: ${e.message}", e)
            null
        }
    }

    // Corregido: Usa recomendarUbicaciones que definimos en ApiService
    suspend fun obtenerUbicacionesDisponibles(loteId: Int): List<String> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.apiService.recomendarUbicaciones(loteId)
            if (response.isSuccessful) {
                // Extrae los códigos devueltos por el algoritmo de recomendaciones
                response.body()?.recomendaciones?.mapNotNull { it.ubicacion } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Corregido: Usa registrarUbicacion que definimos en ApiService
    suspend fun asignarUbicacion(
        loteId: Int,
        palletNumero: Int,
        cantidad: Int,
        ubicacion: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = RegistrarUbicacionRequest(
                qrLote = QrLoteData(
                    loteId = loteId,
                    palletNumero = palletNumero,
                    cantidad = cantidad
                ),
                qrUbicacion = ubicacion
            )

            val response = RetrofitClient.apiService.registrarUbicacion(requestBody)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun terminarUbicar(loteId: Int): Boolean = withContext(Dispatchers.IO) {
        RetrofitClient.apiService.terminarUbicar(loteId).isSuccessful
    }
}