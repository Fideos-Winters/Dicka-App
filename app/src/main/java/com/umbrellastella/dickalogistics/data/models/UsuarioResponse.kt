package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName

data class UsuarioResponse(
    val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("nombreUsuario") val nombreUsuario: String,
    @SerializedName("numEmpleado") val numEmpleado: Int,
    @SerializedName("email") val email: String?,
    // CAMBIO: Recibimos Int, y luego lo tratamos como booleano en la UI
    @SerializedName("IsActive") val IsActive: Int,
    @SerializedName("rol_id") val rol_id: Int,
    val rol: Rol?
) {
    // Propiedad de conveniencia para usar en Compose
    val esActivo: Boolean get() = IsActive == 1
}

data class Rol(
    val id: Int,
    val nombre: String
)