package com.umbrellastella.dickalogistics.data.models

data class LoteDetalleModel(
    val id: Int,
    val folio: Int,
    val lote: String,
    val caducidad: String,
    val numPalets: Int,
    val piezasPalet: Int,
    val cantidad: Int,
    val ubicacion: String,
    val unidadMedida: String
)