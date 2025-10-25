package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.HistorialRuta
import retrofit2.Call
import retrofit2.http.*

interface HistorialRutaApi {

    // 🔹 Listar todo el historial de rutas
    @GET("Historial")
    fun listarHistorial(): Call<List<HistorialRuta>>

    // 🔹 Registrar un nuevo historial
    @POST("Historial")
    fun registrarHistorial(@Body nuevo: HistorialRuta): Call<HistorialRuta>
}
