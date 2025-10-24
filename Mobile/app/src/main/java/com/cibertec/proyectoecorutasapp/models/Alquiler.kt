package com.cibertec.proyectoecorutasapp.models

data class Alquiler(
    val id_alquiler: Int?,
    val usuario: Usuario,
    val vehiculo: Vehiculo,
    val ruta: Ruta,
    val fecha_inicio: String?,
    val fecha_fin: String?,
    val costo: Double?,
    val estado: EstadoAlquiler = EstadoAlquiler.EN_CURSO
) {
    enum class EstadoAlquiler { EN_CURSO, FINALIZADO }
}