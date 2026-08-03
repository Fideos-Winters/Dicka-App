package com.umbrellastella.dickalogistics.data.repositories

import android.util.Log
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.models.LoginRequest
import com.umbrellastella.dickalogistics.data.models.LoginResponse

class AuthRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun iniciarSesion(usuario: String, contrasena: String): Result<LoginResponse> {
        return try {
            val peticion = LoginRequest(
                nombreUsuario = usuario,
                password = contrasena
            )

            val respuesta = apiService.hacerLogin(peticion)

            if (respuesta.isSuccessful && respuesta.body() != null) {
                val loginResponse = respuesta.body()!!

                RetrofitClient.tokenAcceso = loginResponse.token

                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Credenciales incorrectas o usuario no encontrado"))
            }

        } catch (e: Exception) {
            Log.e("AuthRepository", "Error de conexión: ${e.message}")
            Result.failure(Exception("Error de red. Verifica tu conexión al servidor."))
        }
    }
}