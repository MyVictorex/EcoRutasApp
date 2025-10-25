package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.*

class RutaDao(context: Context) {

    private val dbHelper = DbHelper(context)


    fun insertar(ruta: Ruta): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", ruta.nombre)
            put("descripcion", ruta.descripcion)
            put("punto_inicio", ruta.punto_inicio)
            put("punto_fin", ruta.punto_fin)
            put("distancia_km", ruta.distancia_km)
            put("tipo", ruta.tipo.name)
            put("estado", ruta.estado.name)
            put("fecha_creacion", ruta.fecha_creacion)
            put("id_usuario", ruta.usuario?.id_usuario)
        }

        val id = db.insert("ruta", null, values)
        db.close()
        return id
    }


    fun listar(): List<Ruta> {
        val lista = mutableListOf<Ruta>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ruta ORDER BY id_ruta DESC", null)

        while (cursor.moveToNext()) {
            val ruta = Ruta(
                id_ruta = cursor.getInt(cursor.getColumnIndexOrThrow("id_ruta")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                punto_inicio = cursor.getString(cursor.getColumnIndexOrThrow("punto_inicio")),
                punto_fin = cursor.getString(cursor.getColumnIndexOrThrow("punto_fin")),
                distancia_km = cursor.getDouble(cursor.getColumnIndexOrThrow("distancia_km")),
                tipo = TipoRuta.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipo"))),
                estado = EstadoRuta.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado"))),
                fecha_creacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion")),
                usuario = Usuario(
                    id_usuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                    nombre = "",
                    apellido = "",
                    correo = "",
                    password = "",
                    rol = Usuario.Rol.usuario,
                    fecha_registro = null
                )
            )
            lista.add(ruta)
        }

        cursor.close()
        db.close()
        return lista
    }

    fun actualizar(ruta: Ruta): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", ruta.nombre)
            put("descripcion", ruta.descripcion)
            put("punto_inicio", ruta.punto_inicio)
            put("punto_fin", ruta.punto_fin)
            put("distancia_km", ruta.distancia_km)
            put("tipo", ruta.tipo.name)
            put("estado", ruta.estado.name)
            put("id_usuario", ruta.usuario?.id_usuario)
        }

        val filas = db.update("ruta", values, "id_ruta = ?", arrayOf(ruta.id_ruta.toString()))
        db.close()
        return filas
    }

    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete("ruta", "id_ruta = ?", arrayOf(id.toString()))
        db.close()
        return filas
    }
}
