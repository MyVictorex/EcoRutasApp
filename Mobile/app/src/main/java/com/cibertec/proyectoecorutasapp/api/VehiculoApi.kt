package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.Vehiculo
import retrofit2.Call
import retrofit2.http.*

interface VehiculoApi {

    @GET("vehiculos")
    fun listarVehiculos(): Call<List<Vehiculo>>
    @GET("vehiculos/disponibles")
    fun listarDisponibles(): Call<List<Vehiculo>>
    @POST("vehiculos")
    fun registrarVehiculo(@Body nuevo: Vehiculo): Call<Vehiculo>

    @PUT("vehiculos/{id}")
    fun actualizarVehiculo(
        @Path("id") id: Int,
        @Body datos: Vehiculo
    ): Call<Vehiculo>

    @DELETE("vehiculos/{id}")
    fun eliminarVehiculo(@Path("id") id: Int): Call<Void>
}
