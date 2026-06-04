package com.umbrellastella.dickalogistics.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL base para el EMULADOR apuntando a Laravel local en el puerto 8000
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val apiService: DickaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DickaApiService::class.java)
    }
}