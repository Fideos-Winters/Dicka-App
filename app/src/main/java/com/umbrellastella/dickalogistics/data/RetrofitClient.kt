package com.umbrellastella.dickalogistics.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 🌐 URL base para el EMULADOR apuntando a Laravel local en el puerto 8000
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // 🛡️ El interceptor seguro: le pega el token automáticamente a Laravel en las rutas protegidas
    private val interceptorSeguridad = OkHttpClient.Builder().addInterceptor { chain ->
        val peticionOriginal = chain.request()
        val constructorPeticion = peticionOriginal.newBuilder()

        // 🔑 Si el usuario ya inició sesión, jalamos el token guardado en tu SesionUsuario
        SesionUsuario.tokenClave?.let { token ->
            constructorPeticion.addHeader("Authorization", "Bearer $token")
        }

        constructorPeticion.addHeader("Accept", "application/json")
        chain.proceed(constructorPeticion.build())
    }.build()

    // 🏗️ 1. Creamos la instancia de Retrofit (AQUÍ se declara la variable que te faltaba)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(interceptorSeguridad) // Le acoplamos la seguridad de OkHttp
            .addConverterFactory(GsonConverterFactory.create()) // Conversor automático de JSON
            .build()
    }

    // 🚀 2. Ahora sí, usamos la variable 'retrofit' de arriba sin que marque rojo. ¡Cero duplicados!
    val apiService: DickaApiService by lazy {
        retrofit.create(DickaApiService::class.java)
    }
}