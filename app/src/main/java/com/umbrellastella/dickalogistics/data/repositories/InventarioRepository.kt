package com.umbrellastella.dickalogistics.data.repositories

import android.util.Log
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.ProductoModel
import com.umbrellastella.dickalogistics.data.models.LoteDetalleModel

class InventarioRepository {

    // Instancia del servicio único centralizado
    private val apiService = RetrofitClient.apiService

    /**
     * Obtiene el inventario general protegido por token
     */
    suspend fun getInventario(): Result<List<ProductoModel>> {
        return try {
            val respuesta = apiService.obtenerInventarioGeneral()

            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                // Captura respuestas como 401 Unauthorized de Sanctum
                Result.failure(Exception("Error de autenticación o servidor: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al obtener inventario: ${e.message}")
            Result.failure(Exception("Error de red. No se pudo cargar el inventario."))
        }
    }

    /**
     * Obtiene los lotes filtrados por el SKU seleccionado
     */
    suspend fun getLotesPorProducto(sku: String): Result<List<LoteDetalleModel>> {
        return try {
            val respuesta = apiService.obtenerLotesPorProducto(sku)

            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(Exception("Error del servidor al obtener los lotes: ${respuesta.code()}"))
            }
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error en getLotesPorProducto: ${e.message}")
            Result.failure(Exception("Error de red al cargar los lotes del producto."))
        }
    }
}