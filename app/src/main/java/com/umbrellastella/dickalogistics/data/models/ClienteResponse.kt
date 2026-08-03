package com.umbrellastella.dickalogistics.data.models

data class ClienteResponse(
    val id: Int,
    val razonSocial: String,
    val domicilio: String,
    val usuario_id: Int,
    val usuarioModificacion: Int?
)