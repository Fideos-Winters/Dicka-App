package com.umbrellastella.dickalogistics.data

import com.google.gson.annotations.SerializedName

data class RecomendacionResponse(
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("paletsSolicitados") val paletsSolicitados: Int? = null,
    @SerializedName("recomendaciones") val recomendaciones: List<UbicacionRecomendadaModel>? = null
)

data class UbicacionRecomendadaModel(
    @SerializedName("ubicacion") val ubicacion: String? = null,
    @SerializedName("rack") val rack: String? = null,
    @SerializedName("nivel") val nivel: Int? = null,
    @SerializedName("posicion") val posicion: Int? = null
)