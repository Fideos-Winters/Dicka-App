package com.umbrellastella.dickalogistics.data.repositories

import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.ClienteListadoItem
import com.umbrellastella.dickalogistics.data.models.ProductoResponse
import com.umbrellastella.dickalogistics.home.ProductoFormState

class ProductoRepository {

    suspend fun obtenerProductos(): Result<List<ProductoResponse>> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.obtenerProductos()
            if (respuestaHttp.isSuccessful && respuestaHttp.body() != null) {
                Result.success(respuestaHttp.body()!!)
            } else {
                Result.failure(Exception("Error del servidor: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerClientesListado(): Result<List<ClienteListadoItem>> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.obtenerClientesListado()
            if (respuestaHttp.isSuccessful && respuestaHttp.body() != null) {
                Result.success(respuestaHttp.body()!!)
            } else {
                Result.failure(Exception("Error al obtener clientes: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarProducto(producto: ProductoFormState): Result<Unit> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.registrarProducto(producto)
            if (respuestaHttp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al registrar: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarProducto(id: Int, producto: ProductoFormState): Result<Unit> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.actualizarProducto(id, producto)
            if (respuestaHttp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarProducto(id: Int): Result<Unit> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.eliminarProducto(id)
            if (respuestaHttp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}