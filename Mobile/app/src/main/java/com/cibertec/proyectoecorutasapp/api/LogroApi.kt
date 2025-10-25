package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.Logro
import retrofit2.Call
import retrofit2.http.*

interface LogroApi {

    @GET("logros")
    fun listarLogros(): Call<List<Logro>>

    @POST("logros")
    fun registrarLogro(@Body nuevo: Logro): Call<Logro>


    @PUT("logros/{id}")
    fun actualizarLogro(
        @Path("id") id: Int,
        @Body datos: Logro
    ): Call<Logro>
}
