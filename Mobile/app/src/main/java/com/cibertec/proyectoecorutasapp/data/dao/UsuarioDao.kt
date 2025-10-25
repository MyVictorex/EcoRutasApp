package com.cibertec.proyectoecorutasapp.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cibertec.proyectoecorutasapp.data.DbHelper
import com.cibertec.proyectoecorutasapp.models.Usuario

class UsuarioDao(context: Context) {

    private val dbHelper = DbHelper(context)


    fun insertar(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("apellido", usuario.apellido)
            put("correo", usuario.correo)
            put("password", usuario.password)
            put("rol", usuario.rol.name)
            put("fecha_registro", usuario.fecha_registro)
        }
        return db.insert("usuario", null, values)
    }


    fun listar(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM usuario ORDER BY id_usuario DESC", null)

        while (cursor.moveToNext()) {
            val usuario = Usuario(
                id_usuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                rol = Usuario.Rol.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("rol"))), // 👈 convertimos de String → Enum
                fecha_registro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
            )
            lista.add(usuario)
        }

        cursor.close()
        return lista
    }


    fun buscarPorId(id: Int): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuario WHERE id_usuario = ?", arrayOf(id.toString()))
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id_usuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                rol = Usuario.Rol.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("rol"))),
                fecha_registro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
            )
        }

        cursor.close()
        return usuario
    }

    fun actualizar(usuario: Usuario): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", usuario.nombre)
            put("apellido", usuario.apellido)
            put("correo", usuario.correo)
            put("password", usuario.password)
            put("rol", usuario.rol.name) // 👈 convertimos enum → texto
        }
        return db.update("usuario", values, "id_usuario = ?", arrayOf(usuario.id_usuario.toString()))
    }


    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("usuario", "id_usuario = ?", arrayOf(id.toString()))
    }


    fun autenticar(correo: String, password: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuario WHERE correo = ? AND password = ?",
            arrayOf(correo, password)
        )
        var usuario: Usuario? = null

        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id_usuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                rol = Usuario.Rol.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("rol"))),
                fecha_registro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
            )
        }

        cursor.close()
        return usuario
    }
}
