package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName

data class QrLoteContainer(
    @SerializedName("lote_id") val loteId: Int,
    @SerializedName("pallet_numero") val palletNumero: Int
)

data class LoteSalidaModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("qrLote") val qrLote: QrLoteContainer? = null,
    @SerializedName("qrEmbarque") val qrEmbarque: String,
    @SerializedName("paletPiso") val paletPiso: Int,
    @SerializedName("cantidadEntregada") val cantidadEntregada: Int,
    @SerializedName("observaciones") val observaciones: String? = null,
    @SerializedName("lote_id") val loteIdDirecto: Int? = null
)

data class SalidaResponse(
    @SerializedName("message") val message: String
)