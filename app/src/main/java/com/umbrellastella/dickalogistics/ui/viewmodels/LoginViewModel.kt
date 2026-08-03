package com.umbrellastella.dickalogistics.viewmodel

import android.util.Log // 1️⃣ No olvides importar Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbrellastella.dickalogistics.data.RetrofitClient
import com.umbrellastella.dickalogistics.data.SesionUsuario
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

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun hacerLogin(nombreUsuario: String, contrasena: String) {
        if (nombreUsuario.isBlank() || contrasena.isBlank()) {
            _loginState.value = LoginState.Error("Llena todos los campos")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            Log.d("DICKA_LOGIN", "Intento de login para usuario: $nombreUsuario") // 🕵️‍♂️ LOG DE INICIO

            val resultado = repository.iniciarSesion(nombreUsuario, contrasena)

            if (resultado.isSuccess) {
                val cuerpoRespuesta = resultado.getOrNull()

                Log.d("DICKA_LOGIN", "Respuesta exitosa de Laravel: $cuerpoRespuesta") // 🕵️‍♂️ LOG DE ÉXITO

                if (cuerpoRespuesta != null && !cuerpoRespuesta.token.isNullOrEmpty()) {
                    RetrofitClient.tokenAcceso = cuerpoRespuesta.token
                    SesionUsuario.tokenClave = cuerpoRespuesta.token
                    SesionUsuario.rolActual = cuerpoRespuesta.usuario?.rol ?: "Auxiliar"

                    Log.d("DICKA_LOGIN", "Sesión guardada con rol: ${SesionUsuario.rolActual}") // 🕵️‍♂️ LOG DE SESIÓN

                    _loginState.value = LoginState.Success
                } else {
                    Log.e("DICKA_LOGIN", "El token llegó nulo o vacío: $cuerpoRespuesta") // 🚨 LOG DE ALERTA
                    _loginState.value = LoginState.Error("Respuesta incompleta del servidor")
                }
            } else {
                val errorMsg = resultado.exceptionOrNull()?.message ?: "Credenciales incorrectas"
                Log.e("DICKA_LOGIN", "Laravel arrojó error: $errorMsg", resultado.exceptionOrNull()) // 🚨 LOG DE FALLO

                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }
}