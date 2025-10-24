package com.cibertec.proyectoecorutasapp.models

data class Estadistica(
    val id_estadistica: Int? = null,
    val fecha: String,
    val total_usuarios: Int,
    val total_rutas: Int,
    val total_alquileres: Int,
    val co2_ahorrado: Double
)
