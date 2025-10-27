package com.cibertec.proyectoecorutasapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DbHelper(context: Context) : SQLiteOpenHelper(context, "ecorutas.db", null, 3) {


    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE usuario(
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT,
                correo TEXT UNIQUE NOT NULL,
                password TEXT,
                rol TEXT DEFAULT 'usuario',
                fecha_registro TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """)

        db.execSQL("""
            CREATE TABLE ruta(
                id_ruta INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                punto_inicio TEXT,
                punto_fin TEXT,
                distancia_km REAL,
                tipo TEXT,
                estado TEXT DEFAULT 'activa',
                fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
                id_usuario INTEGER,
                FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario)
            )
        """)


        db.execSQL("""
            CREATE TABLE vehiculo(
                id_vehiculo INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
                codigo_qr TEXT UNIQUE,
                disponible INTEGER DEFAULT 1,
                ubicacion_actual TEXT,
                fecha_registro TEXT DEFAULT CURRENT_TIMESTAMP,
                latitud REAL,
                longitud REAL
            )
        """)



        db.execSQL("""
            CREATE TABLE alquiler(
                id_alquiler INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                id_vehiculo INTEGER NOT NULL,
                id_ruta INTEGER NOT NULL,
                fecha_inicio TEXT DEFAULT CURRENT_TIMESTAMP,
                fecha_fin TEXT,
                costo REAL,
                estado TEXT DEFAULT 'EN_CURSO',
                FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario),
                FOREIGN KEY(id_vehiculo) REFERENCES vehiculo(id_vehiculo),
                FOREIGN KEY(id_ruta) REFERENCES ruta(id_ruta)
            )
        """)


        db.execSQL("""
            CREATE TABLE historial_ruta(
                id_historial INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                id_ruta INTEGER NOT NULL,
                fecha_inicio TEXT DEFAULT CURRENT_TIMESTAMP,
                fecha_fin TEXT,
                distancia_recorrida REAL,
                duracion_minutos INTEGER,
                modo_transporte TEXT,
                co2_ahorrado REAL,
                FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario),
                FOREIGN KEY(id_ruta) REFERENCES ruta(id_ruta)
            )
        """)


        db.execSQL("""
            CREATE TABLE logro(
                id_logro INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                descripcion TEXT,
                puntos INTEGER
            )
        """)

        db.execSQL("""
            CREATE TABLE usuario_logro(
                id_usuario INTEGER,
                id_logro INTEGER,
                fecha_obtencion TEXT DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(id_usuario, id_logro),
                FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario),
                FOREIGN KEY(id_logro) REFERENCES logro(id_logro)
            )
        """)


        // 🔹 AGREGAR ESTA TABLA FALTANTE
        db.execSQL("""
            CREATE TABLE estadistica(
                id_estadistica INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha TEXT,
                total_usuarios INTEGER,
                total_rutas INTEGER,
                total_alquileres INTEGER,
                co2_ahorrado REAL
            )
        """)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuario_logro")
        db.execSQL("DROP TABLE IF EXISTS logro")
        db.execSQL("DROP TABLE IF EXISTS historial_ruta")
        db.execSQL("DROP TABLE IF EXISTS alquiler")
        db.execSQL("DROP TABLE IF EXISTS vehiculo")
        db.execSQL("DROP TABLE IF EXISTS ruta")
        db.execSQL("DROP TABLE IF EXISTS usuario")

        db.execSQL("DROP TABLE IF EXISTS estadistica")

        onCreate(db)
    }
}
