package com.umbrellastella.dickalogistics.data.models

// Esta es la caja principal que recibe todo el JSON
data class DashboardResponse(
    val totalPiezasAlmacen: Int,
    val ubicacionesOcupadas: Int,
    val productosPorLote: List<ProductoPorLote>,
    val productosEnAlmacen: List<ProductoEnAlmacen>
)

// Sub-caja para el desglose de productos por lote
data class ProductoPorLote(
    val lote: String,
    val cantidad_total: Int
)

// Sub-caja para el listado detallado de productos en almacén
data class ProductoEnAlmacen(
    val id: Int,
    val sku: String,
    val producto: String,
    val folio: String,
    val lote: String,
    val ubicacion: String,
    val piezas: Int
)