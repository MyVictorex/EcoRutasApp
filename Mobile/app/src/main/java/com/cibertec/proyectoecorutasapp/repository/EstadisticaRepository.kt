package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.EstadisticaApi
import com.cibertec.proyectoecorutasapp.data.dao.EstadisticaDao
import com.cibertec.proyectoecorutasapp.models.Estadistica
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EstadisticaRepository(context: Context) {

    private val dao = EstadisticaDao(context)
    private val api = ApiClient.create(EstadisticaApi::class.java)


    fun listarEstadisticas(onSuccess: (List<Estadistica>) -> Unit, onError: (String) -> Unit) {
        api.listarEstadisticas().enqueue(object : Callback<List<Estadistica>> {
            override fun onResponse(call: Call<List<Estadistica>>, response: Response<List<Estadistica>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { dao.insertar(it) }
                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar())
                }
            }

            override fun onFailure(call: Call<List<Estadistica>>, t: Throwable) {
                onSuccess(dao.listar())
            }
        })
    }


    fun generarEstadistica(onSuccess: (Estadistica) -> Unit, onError: (String) -> Unit) {
        api.generarEstadistica().enqueue(object : Callback<Estadistica> {
            override fun onResponse(call: Call<Estadistica>, response: Response<Estadistica>) {
                if (response.isSuccessful) {
                    val nueva = response.body()!!
                    dao.insertar(nueva)
                    onSuccess(nueva)
                } else {
                    onError("Error al generar estadística en API")
                }
            }

            override fun onFailure(call: Call<Estadistica>, t: Throwable) {
                onError("Error de conexión al generar estadística")
            }
        })
    }
}
