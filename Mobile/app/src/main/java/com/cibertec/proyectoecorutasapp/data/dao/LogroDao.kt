package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.Logro

class LogroDao(context: Context) {

    private val dbHelper = DbHelper(context)


    fun insertar(logro: Logro): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", logro.nombre)
            put("descripcion", logro.descripcion)
            put("puntos", logro.puntos)
        }

        val id = db.insert("logro", null, values)
        db.close()
        return id
    }

    fun listar(): List<Logro> {
        val lista = mutableListOf<Logro>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM logro ORDER BY id_logro DESC", null)

        while (cursor.moveToNext()) {
            val logro = Logro(
                id_logro = cursor.getInt(cursor.getColumnIndexOrThrow("id_logro")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                puntos = cursor.getInt(cursor.getColumnIndexOrThrow("puntos"))
            )
            lista.add(logro)
        }

        cursor.close()
        db.close()
        return lista
    }

    fun actualizar(logro: Logro): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", logro.nombre)
            put("descripcion", logro.descripcion)
            put("puntos", logro.puntos)
        }

        val filas = db.update("logro", values, "id_logro = ?", arrayOf(logro.id_logro.toString()))
        db.close()
        return filas
    }
}
