package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.Alquiler
import retrofit2.Call
import retrofit2.http.*

interface AlquilerApi {


    @GET("alquileres")
    fun listarAlquileres(): Call<List<Alquiler>>


    @POST("alquileres")
    fun registrarAlquiler(@Body nuevo: Alquiler): Call<Alquiler>

    @PUT("alquileres/{id}")
    fun actualizarAlquiler(
        @Path("id") id: Int,
        @Body datos: Alquiler
    ): Call<Alquiler>
}
