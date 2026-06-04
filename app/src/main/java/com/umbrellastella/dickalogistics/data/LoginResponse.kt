package com.umbrellastella.dickalogistics.data

// La respuesta principal del Login
data class LoginResponse(
    val message: String,
    val token: String?,
    val usuario: UsuarioBackend? // Recibimos el objeto del usuario
)

// Sub-modelo para leer los datos del usuario que manda Laravel
data class UsuarioBackend(
    val id: Int,
    val nombre: String,
    val nombreUsuario: String,
    val numEmpleado: Int,
    val rol_id: Int
)