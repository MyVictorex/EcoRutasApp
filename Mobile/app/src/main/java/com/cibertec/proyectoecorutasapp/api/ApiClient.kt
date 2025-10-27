package com.cibertec.proyectoecorutasapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // 🧭 Cambia esto por la IP de tu backend (usa la IP local de tu PC si estás probando en emulador)

    private const val BASE_URL = "https://cardiovascular-unhappy-latonya.ngrok-free.dev/api/"


    // 🔍 Interceptor para ver logs de red
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 🧱 Cliente HTTP con interceptor de token
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor { obtenerTokenActual() }) // ✅ usa el método que devuelve el token
        .build()

    // 🔗 Configurar Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // 🧩 Método genérico para crear servicios API
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    // 🔐 Aquí obtendrás el token almacenado (SharedPreferences, DataStore, etc.)
    private fun obtenerTokenActual(): String? {
        // Si aún no tienes login implementado, puedes dejarlo null
        // Cuando implementes autenticación, lee el token de SharedPreferences:
        // val prefs = context.getSharedPreferences("EcoRutasPrefs", Context.MODE_PRIVATE)
        // return prefs.getString("jwt_token", null)
        return null
    }
}
