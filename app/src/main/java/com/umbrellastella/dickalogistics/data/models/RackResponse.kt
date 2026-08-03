package com.umbrellastella.dickalogistics.data.models



data class NivelRack(
    val nivel: String,
    val ubicaciones: List<PosicionRack> = emptyList()
)



data class DetallePalletRack(
    val palletNumero: Int? = null
)