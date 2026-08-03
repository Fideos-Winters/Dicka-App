package com.umbrellastella.dickalogistics.data

/**
 * 📌 Almacén global y único en memoria para la sesión activa ("Norma ISO").
 * Funciona como la única fuente de verdad mientras el celular esté abierto.
 */
object SesionUsuario {
    // Guarda el Bearer Token para el Interceptor de OkHttp / Retrofit
    var tokenClave: String? = null

    // Guarda el rol recibido ("Administrador", "Montacarguista", "Auxiliar", etc.)
    var rolActual: String = "Auxiliar"

    // Datos generales del usuario conectado (por si quieres mostrar su nombre en el TopBar)
    var usuarioId: Int? = null
    var nombreUsuario: String? = null

    /**
     * Limpia la sesión actual al hacer Logout
     */
    fun cerrarSesion() {
        tokenClave = null
        rolActual = "Auxiliar"
        usuarioId = null
        nombreUsuario = null
    }
}