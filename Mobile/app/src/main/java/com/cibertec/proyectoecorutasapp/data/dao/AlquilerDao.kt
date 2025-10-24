package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.*
import com.cibertec.proyectoecorutasapp.models.Alquiler.EstadoAlquiler

class AlquilerDao(context: Context) {

    private val dbHelper = DbHelper(context)


    fun insertar(alquiler: Alquiler): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", alquiler.usuario.id_usuario)
            put("id_vehiculo", alquiler.vehiculo.id_vehiculo)
            put("id_ruta", alquiler.ruta.id_ruta)
            put("fecha_inicio", alquiler.fecha_inicio)
            put("fecha_fin", alquiler.fecha_fin)
            put("costo", alquiler.costo)
            put("estado", alquiler.estado.name)
        }

        val id = db.insert("alquiler", null, values)
        db.close()
        return id
    }

    // 🔹 LISTAR todos los alquileres
    fun listar(): List<Alquiler> {
        val lista = mutableListOf<Alquiler>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM alquiler ORDER BY id_alquiler DESC", null)

        while (cursor.moveToNext()) {
            val alquiler = Alquiler(
                id_alquiler = cursor.getInt(cursor.getColumnIndexOrThrow("id_alquiler")),
                usuario = Usuario(
                    id_usuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                    nombre = "",
                    apellido = "",
                    correo = "",
                    password = "",
                    rol = Usuario.Rol.USUARIO,
                    fecha_registro = null
                ),
                vehiculo = Vehiculo(
                    id_vehiculo = cursor.getInt(cursor.getColumnIndexOrThrow("id_vehiculo")),
                    tipo = TipoVehiculo.BICICLETA, // valor temporal
                    codigo_qr = "",
                    disponible = true,
                    ubicacion_actual = "",
                    fecha_registro = null,
                    latitud = null,
                    longitud = null
                ),
                ruta = Ruta(
                    id_ruta = cursor.getInt(cursor.getColumnIndexOrThrow("id_ruta")),
                    nombre = "",
                    descripcion = "",
                    punto_inicio = "",
                    punto_fin = "",
                    distancia_km = 0.0,
                    tipo = TipoRuta.BICICLETA,
                    estado = EstadoRuta.activa,
                    fecha_creacion = null,
                    usuario = null
                ),
                fecha_inicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio")),
                fecha_fin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin")),
                costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo")),
                estado = EstadoAlquiler.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
            )
            lista.add(alquiler)
        }

        cursor.close()
        db.close()
        return lista
    }

    fun actualizar(alquiler: Alquiler): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fecha_fin", alquiler.fecha_fin)
            put("costo", alquiler.costo)
            put("estado", alquiler.estado.name)
        }

        val filas = db.update("alquiler", values, "id_alquiler = ?", arrayOf(alquiler.id_alquiler.toString()))
        db.close()
        return filas
    }
}
