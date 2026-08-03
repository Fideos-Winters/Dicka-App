package com.umbrellastella.dickalogistics.data.models
import com.google.gson.annotations.SerializedName
data class LoginRequest(
    @SerializedName("nombreUsuario") val nombreUsuario: String,
    @SerializedName("password") val password: String
)