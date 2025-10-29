package com.cibertec.proyectoecorutasapp.models

data class Ruta(
    val id_ruta: Int?,
    val nombre: String,
    val descripcion: String?,
    val punto_inicio: String?,
    val punto_fin: String?,
    val distancia_km: Double?,
    val tipo: TipoRuta,
    val estado: EstadoRuta = EstadoRuta.activa,
    val fecha_creacion: String?,
    val usuario: Usuario?,

    // Coordenadas de destino (solo en memoria)
    val lat_destino: Double? = null,
    val lng_destino: Double? = null
)


enum class TipoRuta {
    BICICLETA, SCOOTER, MONOPATIN_ELECTRICO, SEGWAY, CARPOOL
}
enum class EstadoRuta { activa, inactiva }
