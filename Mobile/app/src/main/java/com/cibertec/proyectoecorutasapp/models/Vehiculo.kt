package com.cibertec.proyectoecorutasapp.models

data class Vehiculo(
    val id_vehiculo: Int?,
    val tipo: TipoVehiculo,
    val codigo_qr: String?,
    val disponible: Boolean?,
    val ubicacion_actual: String?,
    val fecha_registro: String?,
    val latitud: Double?,
    val longitud: Double?
)

enum class TipoVehiculo {
    BICICLETA, SCOOTER, MONOPATIN_ELECTRICO, SEGWAY, CARPOOL
}
