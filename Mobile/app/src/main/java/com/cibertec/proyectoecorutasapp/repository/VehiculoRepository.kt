package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.VehiculoApi
import com.cibertec.proyectoecorutasapp.data.dao.VehiculoDao
import com.cibertec.proyectoecorutasapp.models.Vehiculo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehiculoRepository(context: Context) {

    private val dao = VehiculoDao(context)
    private val api = ApiClient.create(VehiculoApi::class.java)


    fun listarVehiculos(
        onSuccess: (List<Vehiculo>) -> Unit,
        onError: (String) -> Unit
    ) {
        api.listarVehiculos().enqueue(object : Callback<List<Vehiculo>> {
            override fun onResponse(call: Call<List<Vehiculo>>, response: Response<List<Vehiculo>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()


                    lista.forEach { dao.insertar(it) }

                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar())
                }
            }

            override fun onFailure(call: Call<List<Vehiculo>>, t: Throwable) {

                onSuccess(dao.listar())
            }
        })
    }


    fun registrarVehiculo(
        vehiculo: Vehiculo,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        api.registrarVehiculo(vehiculo).enqueue(object : Callback<Vehiculo> {
            override fun onResponse(call: Call<Vehiculo>, response: Response<Vehiculo>) {
                if (response.isSuccessful) {
                    dao.insertar(response.body()!!)
                    onSuccess()
                } else {
                    onError("Error al registrar en el servidor")
                }
            }

            override fun onFailure(call: Call<Vehiculo>, t: Throwable) {

                dao.insertar(vehiculo)
                onSuccess()
            }
        })
    }


    fun actualizarVehiculo(
        vehiculo: Vehiculo,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        api.actualizarVehiculo(vehiculo.id_vehiculo!!, vehiculo)
            .enqueue(object : Callback<Vehiculo> {
                override fun onResponse(call: Call<Vehiculo>, response: Response<Vehiculo>) {
                    if (response.isSuccessful) {
                        dao.actualizar(vehiculo)
                        onSuccess()
                    } else {
                        onError("Error al actualizar en el servidor")
                    }
                }

                override fun onFailure(call: Call<Vehiculo>, t: Throwable) {
                    dao.actualizar(vehiculo)
                    onSuccess()
                }
            })
    }


    fun eliminarVehiculo(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        api.eliminarVehiculo(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    dao.eliminar(id)
                    onSuccess()
                } else {
                    onError("Error al eliminar en el servidor")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {

                dao.eliminar(id)
                onSuccess()
            }
        })
    }
}
