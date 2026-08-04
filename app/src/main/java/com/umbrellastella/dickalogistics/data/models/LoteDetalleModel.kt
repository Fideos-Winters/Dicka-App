package com.umbrellastella.dickalogistics.data.models
import com.google.gson.annotations.SerializedName
data class LoteDetalleModel(
    @SerializedName("id") val id: Int,
    @SerializedName("folio") val folio: Int?,
    @SerializedName("lote") val lote: String?,
    @SerializedName("caducidad") val caducidad: String?,
    @SerializedName("numPalets") val numPalets: Int?,
    @SerializedName("piezasPalet") val piezasPalet: Int?,
    @SerializedName("cantidad") val cantidad: Int?,
    @SerializedName("ubicacion", alternate = ["ubi", "qr_ubicacion"]) val ubicacion: String?, // 🟢 Atrapa cualquiera de los tres nombres
    @SerializedName("unidadMedida") val unidadMedida: String?
)