package com.umbrellastella.dickalogistics.data.repositories

import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.ClienteResponse
import com.umbrellastella.dickalogistics.home.ClienteFormState

class ClienteRepository {

    suspend fun obtenerClientes(): Result<List<ClienteResponse>> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.obtenerClientes()
            if (respuestaHttp.isSuccessful && respuestaHttp.body() != null) {
                Result.success(respuestaHttp.body()!!)
            } else {
                Result.failure(Exception("Error del servidor: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarCliente(cliente: ClienteFormState): Result<Unit> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.registrarCliente(cliente)
            if (respuestaHttp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al registrar: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarCliente(id: Int, cliente: ClienteFormState): Result<Unit> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.actualizarCliente(id, cliente)
            if (respuestaHttp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar: ${respuestaHttp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarCliente(id: Int): Result<Unit> {
        return try {
            val respuestaHttp = RetrofitClient.apiService.eliminarCliente(id)
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