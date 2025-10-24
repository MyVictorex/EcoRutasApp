package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.*

class HistorialRutaDao(context: Context) {

    private val dbHelper = DbHelper(context)

    // 🔹 Insertar historial (solo guardamos las claves de usuario y ruta)
    fun insertar(historial: HistorialRuta): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_usuario", historial.usuario.id_usuario)
            put("id_ruta", historial.ruta.id_ruta)
            put("fecha_inicio", historial.fecha_inicio)
            put("fecha_fin", historial.fecha_fin)
            put("distancia_recorrida", historial.distancia_recorrida)
            put("duracion_minutos", historial.duracion_minutos)
            put("modo_transporte", historial.modo_transporte.name)
            put("co2_ahorrado", historial.co2_ahorrado)
        }
        return db.insert("historial_ruta", null, values)
    }

    // 🔹 Listar historial (relaciones básicas)
    fun listar(): List<HistorialRuta> {
        val lista = mutableListOf<HistorialRuta>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM historial_ruta ORDER BY fecha_inicio DESC", null)

        while (cursor.moveToNext()) {
            val historial = HistorialRuta(
                id_historial = cursor.getInt(cursor.getColumnIndexOrThrow("id_historial")),
                usuario = Usuario(
                    id_usuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                    nombre = "",
                    apellido = "",
                    correo = "",
                    password = "",
                    rol = Usuario.Rol.USUARIO,
                    fecha_registro = null
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
                distancia_recorrida = cursor.getDouble(cursor.getColumnIndexOrThrow("distancia_recorrida")),
                duracion_minutos = cursor.getInt(cursor.getColumnIndexOrThrow("duracion_minutos")),
                modo_transporte = TipoVehiculo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("modo_transporte"))),
                co2_ahorrado = cursor.getDouble(cursor.getColumnIndexOrThrow("co2_ahorrado"))
            )
            lista.add(historial)
        }

        cursor.close()
        return lista
    }
}
