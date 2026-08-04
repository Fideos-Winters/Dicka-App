package com.umbrellastella.dickalogistics.data

import com.google.gson.GsonBuilder // 1️⃣ Importamos GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val USAR_LOCAL = true
    private const val URL_LOCAL = "http://10.0.2.2:8000/"
    private const val URL_EQUIPO = "http://3.15.238.37"
    private val BASE_URL = if (USAR_LOCAL) URL_LOCAL else URL_EQUIPO

    var tokenAcceso: String? = null

    // 2️⃣ Creamos un Gson "a prueba de balas" (Lenient = true)
    private val gsonBlindado = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo para conectar
            .readTimeout(30, TimeUnit.SECONDS)    // Tiempo para leer la respuesta gigante
            .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo para escribir
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                tokenAcceso?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                requestBuilder.addHeader("Accept", "application/json")
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // 3️⃣ Le pasamos nuestro Gson blindado a Retrofit
            .addConverterFactory(GsonConverterFactory.create(gsonBlindado))
            .build()
    }

    val apiService: DickaApiService by lazy {
        retrofit.create(DickaApiService::class.java)
    }


}