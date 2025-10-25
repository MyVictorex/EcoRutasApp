package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.Ruta
import retrofit2.Call
import retrofit2.http.*

interface RutaApi {


    @GET("rutas")
    fun listarRutas(): Call<List<Ruta>>

    @POST("rutas")
    fun registrarRuta(@Body nuevaRuta: Ruta): Call<Ruta>


    @PUT("rutas/{id}")
    fun actualizarRuta(
        @Path("id") id: Int,
        @Body datos: Ruta
    ): Call<Ruta>

    @DELETE("rutas/{id}")
    fun eliminarRuta(@Path("id") id: Int): Call<Void>
}
