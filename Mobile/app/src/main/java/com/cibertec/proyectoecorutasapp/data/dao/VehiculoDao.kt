package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.*

class VehiculoDao(context: Context) {

    private val dbHelper = DbHelper(context)

    // 🔹 INSERTAR nuevo vehículo
    fun insertar(vehiculo: Vehiculo): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tipo", vehiculo.tipo.name)
            put("codigo_qr", vehiculo.codigo_qr)
            put("disponible", if (vehiculo.disponible==true) 1 else 0)
            put("ubicacion_actual", vehiculo.ubicacion_actual)
            put("fecha_registro", vehiculo.fecha_registro)
            put("latitud", vehiculo.latitud)
            put("longitud", vehiculo.longitud)
        }

        val id = db.insert("vehiculo", null, values)
        db.close()
        return id
    }

    // 🔹 LISTAR todos los vehículos
    fun listar(): List<Vehiculo> {
        val lista = mutableListOf<Vehiculo>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM vehiculo ORDER BY id_vehiculo DESC", null)

        while (cursor.moveToNext()) {
            val vehiculo = Vehiculo(
                id_vehiculo = cursor.getInt(cursor.getColumnIndexOrThrow("id_vehiculo")),
                tipo = TipoVehiculo.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("tipo"))),
                codigo_qr = cursor.getString(cursor.getColumnIndexOrThrow("codigo_qr")),
                disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1,
                ubicacion_actual = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion_actual")),
                fecha_registro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")),
                latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
            )
            lista.add(vehiculo)
        }

        cursor.close()
        db.close()
        return lista
    }

    // 🔹 ACTUALIZAR vehículo
    fun actualizar(vehiculo: Vehiculo): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tipo", vehiculo.tipo.name)
            put("codigo_qr", vehiculo.codigo_qr)
            put("disponible", if (vehiculo.disponible==true) 1 else 0)
            put("ubicacion_actual", vehiculo.ubicacion_actual)
            put("latitud", vehiculo.latitud)
            put("longitud", vehiculo.longitud)
        }

        val filas = db.update("vehiculo", values, "id_vehiculo = ?", arrayOf(vehiculo.id_vehiculo.toString()))
        db.close()
        return filas
    }


    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete("vehiculo", "id_vehiculo = ?", arrayOf(id.toString()))
        db.close()
        return filas
    }
}
