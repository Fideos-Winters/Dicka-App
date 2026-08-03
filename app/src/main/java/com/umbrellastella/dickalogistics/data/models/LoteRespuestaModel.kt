package com.umbrellastella.dickalogistics.data.models

data class LoteRespuestaModel(
    val current_page: Int,
    val per_page: Int,
    val total_ubicados: Int,
    val total_no_ubicados: Int,
    val total_general: Int,
    val data: List<LoteUbicacionModel>
)