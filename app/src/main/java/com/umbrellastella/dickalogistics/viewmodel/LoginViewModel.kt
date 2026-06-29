package com.umbrellastella.dickalogistics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.AuthRepository
import com.umbrellastella.dickalogistics.data.SesionUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados posibles de nuestra pantalla de Login
sealed class LoginState {
    object Idle : LoginState() // Estado inicial (sin hacer nada)
    object Loading : LoginState() // Cargando (esperando al backend)
    object Success : LoginState() // Login exitoso
    data class Error(val message: String) : LoginState() // Error (contraseña mal, etc)
}

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Variable que la pantalla observará para saber qué hacer
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun hacerLogin(usuario: String, contrasena: String) {
        // Validación básica
        if (usuario.isBlank() || contrasena.isBlank()) {
            _loginState.value = LoginState.Error("Llena todos los campos")
            return
        }

        _loginState.value = LoginState.Loading

        // Lanzamos la petición en segundo plano (Corrutina)
        viewModelScope.launch {
            val resultado = repository.iniciarSesion(usuario, contrasena)


            if (resultado.isSuccess) {
                // ¡Todo chido! El token y los datos ya están en el resultado
                val cuerpoRespuesta = resultado.getOrNull()
                SesionUsuario.tokenClave = cuerpoRespuesta?.token
                SesionUsuario.rolActual = when (cuerpoRespuesta?.usuario?.rol_id) {
                    1 -> "Administrador"
                    2 -> "Montacarguista"
                    else -> "Auxiliar"
                }

                _loginState.value = LoginState.Success
            } else {
                // Hubo un error (credenciales incorrectas o servidor caído)
                _loginState.value = LoginState.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}