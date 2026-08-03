package com.umbrellastella.dickalogistics.data.repositories

import android.util.Log
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.UsuarioRequest
import com.umbrellastella.dickalogistics.data.models.UsuarioResponse

class UsuarioRepository {
    suspend fun obtenerUsuarios(): Result<List<UsuarioResponse>> {
        return try {
            val res = RetrofitClient.apiService.getUsuarios()
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception("Error ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarUsuario(request: UsuarioRequest): Result<Unit> {
        return try {
            val res = RetrofitClient.apiService.storeUsuario(request)
            if (res.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("API_FAIL", "Error ${res.code()}: ${res.errorBody()?.string()}")
                Result.failure(Exception("Fallo en registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarUsuario(id: Int, request: UsuarioRequest): Result<Unit> {
        return try {
            val res = RetrofitClient.apiService.updateUsuario(id, request)
            if (res.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("API_FAIL", "Error ${res.code()}: ${res.errorBody()?.string()}")
                Result.failure(Exception("Fallo en actualización"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarUsuario(id: Int): Result<Unit> {
        return try {
            val res = RetrofitClient.apiService.destroyUsuario(id)
            if (res.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Fallo al eliminar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}