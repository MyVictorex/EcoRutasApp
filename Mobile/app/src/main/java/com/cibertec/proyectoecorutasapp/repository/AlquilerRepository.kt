package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.AlquilerApi
import com.cibertec.proyectoecorutasapp.data.dao.AlquilerDao
import com.cibertec.proyectoecorutasapp.models.Alquiler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlquilerRepository(context: Context) {

    private val dao = AlquilerDao(context)
    private val api = ApiClient.create(AlquilerApi::class.java)


    fun listarAlquileres(onSuccess: (List<Alquiler>) -> Unit, onError: (String) -> Unit) {
        api.listarAlquileres().enqueue(object : Callback<List<Alquiler>> {
            override fun onResponse(call: Call<List<Alquiler>>, response: Response<List<Alquiler>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { dao.insertar(it) }
                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar())
                }
            }

            override fun onFailure(call: Call<List<Alquiler>>, t: Throwable) {
                onSuccess(dao.listar())
            }
        })
    }


    fun registrarAlquiler(alquiler: Alquiler, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.registrarAlquiler(alquiler).enqueue(object : Callback<Alquiler> {
            override fun onResponse(call: Call<Alquiler>, response: Response<Alquiler>) {
                if (response.isSuccessful) {
                    dao.insertar(response.body()!!)
                    onSuccess()
                } else {
                    onError("Error al registrar alquiler en API")
                }
            }

            override fun onFailure(call: Call<Alquiler>, t: Throwable) {
                dao.insertar(alquiler)
                onSuccess()
            }
        })
    }


    fun actualizarAlquiler(alquiler: Alquiler, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.actualizarAlquiler(alquiler.id_alquiler!!, alquiler).enqueue(object : Callback<Alquiler> {
            override fun onResponse(call: Call<Alquiler>, response: Response<Alquiler>) {
                if (response.isSuccessful) {
                    dao.actualizar(alquiler)
                    onSuccess()
                } else {
                    onError("Error al actualizar alquiler en API")
                }
            }

            override fun onFailure(call: Call<Alquiler>, t: Throwable) {
                dao.actualizar(alquiler)
                onSuccess()
            }
        })
    }
}
