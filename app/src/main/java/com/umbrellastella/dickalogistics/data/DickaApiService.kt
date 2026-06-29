package com.umbrellastella.dickalogistics.data


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DickaApiService {
    @POST("api/login")
    suspend fun hacerLogin(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("api/dashboard/estadisticas")
    suspend fun obtenerEstadisticas(): DashboardResponse

    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoResponse>
}