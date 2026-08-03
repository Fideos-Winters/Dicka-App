package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName

data class ProductoModel(
    val sku: String,
    @SerializedName("nombre") val nombreProducto: String,
    val cliente: String,
    val ubicacion: String,
    val totalPiezas: String
)