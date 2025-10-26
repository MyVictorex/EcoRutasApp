package com.cibertec.proyectoecorutasapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.proyectoecorutasapp.databinding.AcitivityEstadisticasBinding
import com.cibertec.proyectoecorutasapp.models.Estadistica
import com.cibertec.proyectoecorutasapp.repository.EstadisticaRepository

class EstadisticasActivity : AppCompatActivity() {

    private lateinit var b: AcitivityEstadisticasBinding
    private lateinit var repository: EstadisticaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = AcitivityEstadisticasBinding.inflate(layoutInflater)
        setContentView(b.root)

        // 🔹 Toolbar
        setSupportActionBar(b.toolbarEstadisticas)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbarEstadisticas.setNavigationOnClickListener { finish() }

        repository = EstadisticaRepository(this)

        // 🔹 Cargar las estadísticas al abrir la pantalla
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        repository.listarEstadisticas(
            onSuccess = { lista ->
                if (lista.isNotEmpty()) {
                    // Mostrar la más reciente
                    mostrarEstadisticas(lista.last())
                } else {
                    // Si no hay datos, generamos uno nuevo
                    generarNuevaEstadistica()
                }
            },
            onError = { mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun generarNuevaEstadistica() {
        repository.generarEstadistica(
            onSuccess = {
                mostrarEstadisticas(it)
                Toast.makeText(this, "Nueva estadística generada ✅", Toast.LENGTH_SHORT).show()
            },
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun mostrarEstadisticas(e: Estadistica) {
        b.tvCo2Ahorrado.text = "${e.co2_ahorrado} kg"
        b.tvKmRecorridos.text = "${e.total_alquileres} viajes"
        b.tvRutasCompletadas.text = "${e.total_rutas} rutas"
    }
}
