package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.HistorialRutaApi
import com.cibertec.proyectoecorutasapp.data.dao.HistorialRutaDao
import com.cibertec.proyectoecorutasapp.models.HistorialRuta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialRutaRepository(context: Context) {

    private val dao = HistorialRutaDao(context)
    private val api = ApiClient.create(HistorialRutaApi::class.java)

    // 🔹 Listar historial (desde API o base local)
    fun listarHistorial(onSuccess: (List<HistorialRuta>) -> Unit, onError: (String) -> Unit) {
        api.listarHistorial().enqueue(object : Callback<List<HistorialRuta>> {
            override fun onResponse(call: Call<List<HistorialRuta>>, response: Response<List<HistorialRuta>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { dao.insertar(it) }
                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar())
                }
            }

            override fun onFailure(call: Call<List<HistorialRuta>>, t: Throwable) {
                onSuccess(dao.listar())
            }
        })
    }

    // 🔹 Registrar historial completo (objeto)
    fun registrarHistorial(historial: HistorialRuta, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.registrarHistorial(historial).enqueue(object : Callback<HistorialRuta> {
            override fun onResponse(call: Call<HistorialRuta>, response: Response<HistorialRuta>) {
                if (response.isSuccessful) {
                    dao.insertar(response.body()!!)
                    onSuccess()
                } else {
                    onError("Error al registrar historial en API")
                }
            }

            override fun onFailure(call: Call<HistorialRuta>, t: Throwable) {
                dao.insertar(historial)
                onSuccess()
            }
        })
    }


}
