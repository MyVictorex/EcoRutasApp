package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.Estadistica

class EstadisticaDao(context: Context) {

    private val dbHelper = DbHelper(context)

    fun insertar(estadistica: Estadistica): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("fecha", estadistica.fecha)
            put("total_usuarios", estadistica.total_usuarios)
            put("total_rutas", estadistica.total_rutas)
            put("total_alquileres", estadistica.total_alquileres)
            put("co2_ahorrado", estadistica.co2_ahorrado)
        }
        return db.insert("estadistica", null, values)
    }


    fun listar(): List<Estadistica> {
        val lista = mutableListOf<Estadistica>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM estadistica ORDER BY fecha DESC", null)

        while (cursor.moveToNext()) {
            val e = Estadistica(
                id_estadistica = cursor.getInt(cursor.getColumnIndexOrThrow("id_estadistica")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                total_usuarios = cursor.getInt(cursor.getColumnIndexOrThrow("total_usuarios")),
                total_rutas = cursor.getInt(cursor.getColumnIndexOrThrow("total_rutas")),
                total_alquileres = cursor.getInt(cursor.getColumnIndexOrThrow("total_alquileres")),
                co2_ahorrado = cursor.getDouble(cursor.getColumnIndexOrThrow("co2_ahorrado"))
            )
            lista.add(e)
        }

        cursor.close()
        return lista
    }
}
