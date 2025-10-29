package com.cibertec.proyectoecorutasapp.models

data class HistorialRutaDTO(
    val idRuta: Int? = null,           // null para rutas libres
    val usuarioId: String,             // id del usuario (Firebase UID)
    val modo: String,                  // "BICICLETA", "SCOOTER", etc.
    val ruta: List<List<Double>>,      // [[lat,lng], [lat,lng], ...]
    val distanciaRecorrida: Double,
    val duracionMinutos: Int
)
