package com.cibertec.proyectoecorutasapp.api

import com.cibertec.proyectoecorutasapp.models.LoginResponse
import com.cibertec.proyectoecorutasapp.models.Usuario
import retrofit2.Call
import retrofit2.http.*

interface UsuarioApi {

    @GET("usuarios")
    fun listarUsuarios(): Call<List<Usuario>>

    @GET("usuarios/{id}")
    fun obtenerUsuario(@Path("id") id: Int): Call<Usuario>

    @POST("usuarios")
    fun registrarUsuario(@Body usuario: Usuario): Call<Usuario>

    @PUT("usuarios/{id}")
    fun actualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: Usuario
    ): Call<Usuario>

    @DELETE("usuarios/{id}")
    fun eliminarUsuario(@Path("id") id: Int): Call<Void>

    @POST("usuarios/login")
    fun login(@Body credenciales: Map<String, String>): Call<LoginResponse>
}
