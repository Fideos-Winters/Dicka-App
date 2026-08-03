package com.umbrellastella.dickalogistics.data.models

import com.google.gson.annotations.SerializedName

data class QrLoteModel(
    @SerializedName("lot") val lot: String?,
    @SerializedName("codigo_pallet") val codigoPallet: String?,
    @SerializedName("pallet_numero") val palletNumero: Int?,
    @SerializedName("cantidad") val cantidad: Int?,
    @SerializedName("producto") val producto: String?,
    @SerializedName("fecha_ingreso") val fechaIngreso: String?,
    @SerializedName("ubicacion") val ubicacion: String?
)