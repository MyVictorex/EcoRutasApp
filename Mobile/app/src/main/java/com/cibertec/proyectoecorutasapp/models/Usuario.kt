package com.cibertec.proyectoecorutasapp.models

data class Usuario(
    val id_usuario: Int?,
    val nombre: String,
    val apellido: String?,
    val correo: String,
    val password: String?,
    val rol: Rol = Rol.USUARIO,
    val fecha_registro: String?
) {
    enum class Rol { USUARIO, ADMIN }
}
