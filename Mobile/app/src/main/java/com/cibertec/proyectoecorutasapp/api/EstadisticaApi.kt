package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.Estadistica
import retrofit2.Call
import retrofit2.http.*

interface EstadisticaApi {

    // 🔹 Listar estadísticas
    @GET("estadisticas")
    fun listarEstadisticas(): Call<List<Estadistica>>

    // 🔹 Generar una nueva estadística
    @POST("estadisticas")
    fun generarEstadistica(): Call<Estadistica>
}
