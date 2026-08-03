package com.umbrellastella.dickalogistics.data.models

data class RackSimple(val _id: String, val nombre: String)
data class Ubicacion(
    val id: Int,
    val rack: String,
    val nivel: String,
    val posicion: String,
    val estado: String // 'vacio', 'ocupado', 'mantenimiento'
)

