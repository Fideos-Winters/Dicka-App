package com.umbrellastella.dickalogistics.data.repositories

import android.util.Log
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.LoteEntradaModel
import okhttp3.ResponseBody // <-- Importación necesaria para el PDF

class LoteRepository {
    // Asegúrate de que 'apiService' coincida exactamente con la propiedad definida en tu RetrofitClient
    private val apiService = RetrofitClient.apiService

    suspend fun obtenerLotes(): Result<List<LoteEntradaModel>> {
        return try {
            val respuesta = apiService.obtenerLotes()
            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(Exception("Error al obtener lotes: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Log.e("LoteRepository", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun guardarLote(lote: LoteEntradaModel): Result<Unit> {
        return try {
            val respuesta = apiService.guardarLote(lote)
            if (respuesta.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error de servidor al guardar: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarLote(id: Int, lote: LoteEntradaModel): Result<Unit> {
        return try {
            // CORRECCIÓN: Se envían ambos parámetros (id e interfaz de modelo) a la llamada de Retrofit
            val respuesta = apiService.actualizarLote(id, lote)
            if (respuesta.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error de servidor al actualizar: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NUEVA FUNCIÓN: Descarga el PDF de las etiquetas
    suspend fun descargarEtiquetaPdf(loteId: Int): Result<ResponseBody> {
        return try {
            // Asegúrate de haber agregado descargarEtiquetasPdf en tu interfaz de API
            val respuesta = apiService.descargarEtiquetasPdf(loteId)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(Exception("Error al descargar PDF: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Log.e("LoteRepository", "Error al descargar PDF: ${e.message}")
            Result.failure(e)
        }
    }
}