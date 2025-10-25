package com.cibertec.proyectoecorutasapp.models

data class LoginResponse(
    val token: String,
    val usuario: Usuario
)