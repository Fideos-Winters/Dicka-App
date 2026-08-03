package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName

data class LoteEntradaModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("folio") val folio: Int,
    @SerializedName("producto_id") val productoId: Int,
    @SerializedName("lote") val lote: String,
    @SerializedName("caducidad") val caducidad: String,
    @SerializedName("fechaRecibido") val fechaRecibido: String?,
    @SerializedName("numPalets") val numPalets: Int,
    @SerializedName("piezasPalet") val piezasPalet: Int,
    @SerializedName("unidadMedida") val unidadMedida: String,
    @SerializedName("observaciones") val observaciones: String?,
    @SerializedName("ubi") val ubi: String = "No Ubicado",
    // Asegurar que esta propiedad esté declarada exactamente así:
    @SerializedName("producto") val productoRelacion: ProductoRelacionModel? = null
)

data class ProductoRelacionModel(
    val id: Int,
    val sku: String,
    val nombre: String
)