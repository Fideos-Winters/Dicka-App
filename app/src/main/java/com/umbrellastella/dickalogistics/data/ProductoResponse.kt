package com.umbrellastella.dickalogistics.data

// Cada objeto del arreglo que regresa tu index() de Laravel
data class ProductoResponse(
    val id: Int,
    val sku: String,
    val nombre: String,
    val cliente: String, // Usado como Categoría
    val propiedades: String,
    val caracteristicas: String,
    val usuario_id: Int?,
    val usuarioModificacion: Int?
)