package com.cibertec.proyectoecorutasapp.repository

import android.content.Context
import com.cibertec.proyectoecorutasapp.api.ApiClient
import com.cibertec.proyectoecorutasapp.api.UsuarioApi
import com.cibertec.proyectoecorutasapp.data.dao.UsuarioDao
import com.cibertec.proyectoecorutasapp.models.LoginResponse
import com.cibertec.proyectoecorutasapp.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioRepository(context: Context) {

    private val dao = UsuarioDao(context)
    private val api = ApiClient.create(UsuarioApi::class.java)

    // 🔹 LISTAR usuarios (preferencia API, fallback local)
    fun listarUsuarios(onSuccess: (List<Usuario>) -> Unit, onError: (String) -> Unit) {
        api.listarUsuarios().enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    lista.forEach { dao.insertar(it) } // sincroniza con SQLite
                    onSuccess(lista)
                } else {
                    onSuccess(dao.listar()) // sin conexión → local
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                onSuccess(dao.listar()) // sin conexión → local
            }
        })
    }

    // 🔹 INSERTAR usuario
    fun registrarUsuario(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.registrarUsuario(usuario).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    dao.insertar(response.body()!!)
                    onSuccess()
                } else {
                    onError("Error al registrar usuario")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                dao.insertar(usuario)
                onSuccess()
            }
        })
    }

    // 🔹 ACTUALIZAR usuario
    fun actualizarUsuario(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.actualizarUsuario(usuario.id_usuario!!, usuario).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    dao.actualizar(usuario)
                    onSuccess()
                } else {
                    onError("Error al actualizar usuario")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                dao.actualizar(usuario)
                onSuccess()
            }
        })
    }

    // 🔹 ELIMINAR usuario
    fun eliminarUsuario(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        api.eliminarUsuario(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    dao.eliminar(id)
                    onSuccess()
                } else {
                    onError("Error al eliminar usuario")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                dao.eliminar(id)
                onSuccess()
            }
        })
    }


    fun autenticar(
        correo: String,
        password: String,
        onSuccess: (Usuario?) -> Unit,
        onError: (String) -> Unit
    ) {
        val credenciales = mapOf("correo" to correo, "password" to password)

        api.login(credenciales).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val usuario = body?.usuario

                    if (usuario != null) {
                        dao.insertar(usuario)
                        onSuccess(usuario)
                    } else {
                        onError("Usuario no encontrado en respuesta")
                    }
                } else {
                    onError("Credenciales inválidas")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                val local = dao.autenticar(correo, password)
                onSuccess(local)
            }
        })
    }
    fun obtenerUsuarioPorId(
        id: Int,
        onSuccess: (Usuario) -> Unit,
        onError: (String) -> Unit
    ) {
        api.obtenerUsuario(id).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful && response.body() != null) {
                    val usuario = response.body()!!
                    dao.insertar(usuario)
                    onSuccess(usuario)
                } else {
                    val local = dao.buscarPorId(id)
                    if (local != null) onSuccess(local)
                    else onError("Usuario no encontrado")
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                val local = dao.buscarPorId(id)
                if (local != null) onSuccess(local)
                else onError("Error de conexión: ${t.message}")
            }
        })
    }
}
