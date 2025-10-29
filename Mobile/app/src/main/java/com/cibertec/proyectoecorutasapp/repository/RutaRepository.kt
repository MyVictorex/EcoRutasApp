package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.RutaApi
import com.cibertec.proyectoecorutasapp.data.dao.RutaDao
import com.cibertec.proyectoecorutasapp.models.Ruta
import com.cibertec.proyectoecorutasapp.models.TipoRuta
import com.cibertec.proyectoecorutasapp.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RutaRepository(context: Context) {

    private val dao = RutaDao(context)
    private val api = ApiClient.create(RutaApi::class.java)


    fun listarRutas(onSuccess: (List<Ruta>) -> Unit, onError: (String) -> Unit) {
        api.listarRutas().enqueue(object : Callback<List<Ruta>> {
            override fun onResponse(call: Call<List<Ruta>>, response: Response<List<Ruta>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { dao.insertar(it) }
                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar())
                }
            }

            override fun onFailure(call: Call<List<Ruta>>, t: Throwable) {
                onSuccess(dao.listar())
            }
        })
    }


    fun registrarRuta(ruta: Ruta, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.registrarRuta(ruta).enqueue(object : Callback<Ruta> {
            override fun onResponse(call: Call<Ruta>, response: Response<Ruta>) {
                if (response.isSuccessful) {
                    dao.insertar(response.body()!!)
                    onSuccess()
                } else {
                    onError("Error al registrar ruta en API")
                }
            }

            override fun onFailure(call: Call<Ruta>, t: Throwable) {
                dao.insertar(ruta)
                onSuccess()
            }
        })
    }


    fun actualizarRuta(ruta: Ruta, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.actualizarRuta(ruta.id_ruta!!, ruta).enqueue(object : Callback<Ruta> {
            override fun onResponse(call: Call<Ruta>, response: Response<Ruta>) {
                if (response.isSuccessful) {
                    dao.actualizar(ruta)
                    onSuccess()
                } else {
                    onError("Error al actualizar ruta en API")
                }
            }

            override fun onFailure(call: Call<Ruta>, t: Throwable) {
                dao.actualizar(ruta)
                onSuccess()
            }
        })
    }


    fun eliminarRuta(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.eliminarRuta(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    dao.eliminar(id)
                    onSuccess()
                } else {
                    onError("Error al eliminar ruta en API")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                dao.eliminar(id)
                onSuccess()
            }
        })
    }

    fun crearRutaAutomatica(
        nombre: String,
        puntoInicio: String,
        puntoFin: String,
        distancia: Double,
        tipo: TipoRuta,
        idUsuario: Int,
        onSuccess: (Ruta) -> Unit,
        onError: (String) -> Unit
    ) {
        val ruta = Ruta(
            id_ruta = null,
            nombre = nombre,
            descripcion = "Ruta generada automáticamente desde el mapa",
            punto_inicio = puntoInicio,
            punto_fin = puntoFin,
            distancia_km = distancia,
            tipo = tipo,
            usuario = Usuario(id_usuario = idUsuario, nombre = "", apellido = "", correo = "", password = "", rol = Usuario.Rol.usuario, fecha_registro = null),
            fecha_creacion = null
        )

        api.registrarRuta(ruta).enqueue(object : Callback<Ruta> {
            override fun onResponse(call: Call<Ruta>, response: Response<Ruta>) {
                if (response.isSuccessful) {
                    onSuccess(response.body()!!)
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Ruta>, t: Throwable) {
                onError(t.message ?: "Error al registrar ruta")
            }
        })
    }





}
