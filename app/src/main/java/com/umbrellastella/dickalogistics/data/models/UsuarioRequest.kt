package com.umbrellastella.dickalogistics.data.models

data class UsuarioRequest(
    val nombre: String,
    val nombreUsuario: String,
    val numEmpleado: Int,
    val email: String?,
    val password: String?,
    val IsActive: Boolean,
    val rol_id: Int
)