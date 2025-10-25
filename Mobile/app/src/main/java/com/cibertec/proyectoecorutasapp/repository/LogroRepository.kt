package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.LogroApi
import com.cibertec.proyectoecorutasapp.data.dao.LogroDao
import com.cibertec.proyectoecorutasapp.models.Logro
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogroRepository(context: Context) {

    private val dao = LogroDao(context)
    private val api = ApiClient.create(LogroApi::class.java)


    fun listarLogros(onSuccess: (List<Logro>) -> Unit, onError: (String) -> Unit) {
        api.listarLogros().enqueue(object : Callback<List<Logro>> {
            override fun onResponse(call: Call<List<Logro>>, response: Response<List<Logro>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { dao.insertar(it) }
                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar())
                }
            }

            override fun onFailure(call: Call<List<Logro>>, t: Throwable) {
                onSuccess(dao.listar())
            }
        })
    }


    fun registrarLogro(logro: Logro, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.registrarLogro(logro).enqueue(object : Callback<Logro> {
            override fun onResponse(call: Call<Logro>, response: Response<Logro>) {
                if (response.isSuccessful) {
                    dao.insertar(response.body()!!)
                    onSuccess()
                } else {
                    onError("Error al registrar logro en API")
                }
            }

            override fun onFailure(call: Call<Logro>, t: Throwable) {
                dao.insertar(logro)
                onSuccess()
            }
        })
    }


    fun actualizarLogro(logro: Logro, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.actualizarLogro(logro.id_logro!!, logro).enqueue(object : Callback<Logro> {
            override fun onResponse(call: Call<Logro>, response: Response<Logro>) {
                if (response.isSuccessful) {
                    dao.actualizar(logro)
                    onSuccess()
                } else {
                    onError("Error al actualizar logro en API")
                }
            }

            override fun onFailure(call: Call<Logro>, t: Throwable) {
                dao.actualizar(logro)
                onSuccess()
            }
        })
    }
}
