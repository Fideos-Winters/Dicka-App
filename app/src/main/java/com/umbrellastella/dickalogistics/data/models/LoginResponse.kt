package com.umbrellastella.dickalogistics.data.models
import com.google.gson.annotations.SerializedName
// La respuesta principal del Login
data class LoginResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("usuario") val usuario: UsuarioBackend?
)

// 👤 Sub-modelo del usuario (AHORA EL ROL ES UN STRING)
data class UsuarioBackend(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("nombreUsuario") val nombreUsuario: String,
    @SerializedName("email") val email: String?,
    @SerializedName("rol") val rol: String? // Cambió de Int (rol_id) a String? ("Administrador", etc.)
)