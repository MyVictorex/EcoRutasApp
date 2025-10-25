package com.cibertec.proyectoecorutasapp

import android.app.Application
import android.content.Context

class AppContext : Application() {

    companion object {
        lateinit var instance: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
    }
}
