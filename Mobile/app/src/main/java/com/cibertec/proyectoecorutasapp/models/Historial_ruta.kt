package com.cibertec.proyectoecorutasapp.models

data class HistorialRuta(
    val id_historial: Int?,
    val usuario: Usuario,
    val ruta: Ruta,
    val fecha_inicio: String?,
    val fecha_fin: String?,
    val distancia_recorrida: Double?,
    val duracion_minutos: Int?,
    val modo_transporte: TipoVehiculo,
    val co2_ahorrado: Double?
)
