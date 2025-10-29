package com.cibertec.proyectoecorutasapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {



    private const val BASE_URL = "https://cardiovascular-unhappy-latonya.ngrok-free.dev/api/"



    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor { obtenerTokenActual() }) // ✅ usa el método que devuelve el token
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // 🧩 Método genérico para crear servicios API
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }


    private fun obtenerTokenActual(): String? {

        return null
    }
}
