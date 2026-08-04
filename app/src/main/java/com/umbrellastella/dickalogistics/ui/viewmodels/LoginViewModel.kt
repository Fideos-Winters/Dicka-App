package com.umbrellastella.dickalogistics.viewmodel

import android.app.Application // 1️⃣ Importamos Application para acceder al contexto
import android.util.Log
import androidx.lifecycle.AndroidViewModel // 2️⃣ Cambiamos ViewModel por AndroidViewModel para tener el context
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.SessionManager // 3️⃣ Importamos nuestro gestor de DataStore
import com.umbrellastella.dickalogistics.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

// ⚠️ Nota: Cambiamos a AndroidViewModel(application) para poder inicializar SessionManager(application)
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository()

    // 4️⃣ Inicializamos el DataStore usando el contexto de la app
    private val sessionManager = SessionManager(application)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun hacerLogin(nombreUsuario: String, contrasena: String) {
        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            _loginState.value = LoginState.Error("Llena todos los campos")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            Log.d("DICKA_LOGIN", "Intento de login para usuario: $nombreUsuario")

            val resultado = repository.iniciarSesion(nombreUsuario, contrasena)

            if (resultado.isSuccess) {
                val cuerpoRespuesta = resultado.getOrNull()

                Log.d("DICKA_LOGIN", "Respuesta exitosa de Laravel: $cuerpoRespuesta")

                if (cuerpoRespuesta != null && !cuerpoRespuesta.token.isNullOrEmpty()) {
                    val token = cuerpoRespuesta.token
                    val rol = cuerpoRespuesta.usuario?.rol ?: "Auxiliar"

                    // 5️⃣ GUARDAR EN DATASTORE (Persistencia real ante cierres de app)
                    sessionManager.guardarSesion(token, rol)

                    // 6️⃣ Mantener asignados los valores en caliente para Retrofit
                    RetrofitClient.tokenAcceso = token

                    Log.d("DICKA_LOGIN", "Sesión guardada exitosamente con DataStore. Rol: $rol")

                    _loginState.value = LoginState.Success
                } else {
                    Log.e("DICKA_LOGIN", "El token llegó nulo o vacío: $cuerpoRespuesta")
                    _loginState.value = LoginState.Error("Respuesta incompleta del servidor")
                }
            } else {
                val errorMsg = resultado.exceptionOrNull()?.message ?: "Credenciales incorrectas"
                Log.e("DICKA_LOGIN", "Laravel arrojó error: $errorMsg", resultado.exceptionOrNull())

                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }

    fun cerrarSesion(onSesionCerrada: () -> Unit) {
        viewModelScope.launch {
            // 1. Limpiar el almacenamiento interno (DataStore)
            sessionManager.cerrarSesion()

            // 2. Resetear el token en el cliente HTTP de Retrofit
            RetrofitClient.tokenAcceso = null

            // 3. Ejecutar la navegación hacia el Login
            onSesionCerrada()
        }
    }
}