package com.umbrellastella.dickalogistics.data

data class LoginRequest(
    val nombreUsuario: String, // Cambiado de 'email' a 'nombreUsuario'
    val password: String
)