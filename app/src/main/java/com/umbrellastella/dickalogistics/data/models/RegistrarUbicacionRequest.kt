package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName

data class RegistrarUbicacionRequest(
    @SerializedName("qrLote") val qrLote: QrLoteData,
    @SerializedName("qrUbicacion") val qrUbicacion: String
)

data class QrLoteData(
    @SerializedName("lote_id") val loteId: Int,
    @SerializedName("pallet_numero") val palletNumero: Int,
    @SerializedName("cantidad") val cantidad: Int
)