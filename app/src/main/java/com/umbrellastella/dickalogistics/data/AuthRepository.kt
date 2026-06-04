package com.umbrellastella.dickalogistics.data

import android.util.Log

class AuthRepository {
    // Mandamos llamar a la configuración de Retrofit que armaste en el paso anterior
    private val apiService = RetrofitClient.apiService

    // Usamos 'suspend' porque esta función va a internet y debe correr en segundo plano (Corrutinas)
    suspend fun iniciarSesion(usuario: String, contrasena: String): Result<LoginResponse> {
        return try {
            // 1. Empaquetamos los datos en la cajita que Laravel espera
            val peticion = LoginRequest(
                nombreUsuario = usuario,
                password = contrasena
            )

            // 2. Hacemos la petición POST a través de Retrofit
            val respuesta = apiService.hacerLogin(peticion)

            // 3. Revisamos qué nos contestó Laravel
            if (respuesta.isSuccessful && respuesta.body() != null) {
                // Laravel nos dio un código 200 (Todo OK) y nos mandó el JSON
                Result.success(respuesta.body()!!)
            } else {
                // Laravel nos dio un error (Ej. 401 Credenciales incorrectas)
                Result.failure(Exception("Credenciales incorrectas o usuario no encontrado"))
            }

        } catch (e: Exception) {
            // 4. Si la app no tiene internet o el servidor localhost está apagado
            Log.e("AuthRepository", "Error de conexión: ${e.message}")
            Result.failure(Exception("Error de red. Verifica tu conexión al servidor."))
        }
    }
}