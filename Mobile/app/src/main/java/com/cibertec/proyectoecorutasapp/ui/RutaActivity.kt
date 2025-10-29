package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.proyectoecorutasapp.databinding.AcitivityRutaBinding
import com.cibertec.proyectoecorutasapp.repository.RutaRepository
import com.cibertec.proyectoecorutasapp.ui.adapter.RutaAdapter
import com.google.android.gms.maps.model.LatLng

class RutaActivity : AppCompatActivity() {

    private lateinit var binding: AcitivityRutaBinding
    private lateinit var adapter: RutaAdapter
    private lateinit var repository: RutaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityRutaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = RutaRepository(this)
        adapter = RutaAdapter(emptyList())

        binding.rvRutas.layoutManager = LinearLayoutManager(this)
        binding.rvRutas.adapter = adapter

        // Configurar clic en cada ruta
        adapter.setOnItemClickListener { ruta ->
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("lat_destino", ruta.lat_destino)
                putExtra("lng_destino", ruta.lng_destino)
            }
            startActivity(intent)
        }

        // Cargar rutas desde el repositorio
        cargarRutas()
    }

    private fun cargarRutas() {
        repository.listarRutas(
            onSuccess = { lista ->
                val listaConCoords = lista.map { ruta ->
                    val coords = when (ruta.nombre) {
                        "Ruta Central" -> LatLng(-12.0464, -77.0300)
                        "Ruta Verde" -> LatLng(-12.0975, -77.0365)
                        "Costa Ride" -> LatLng(-12.1250, -77.0260)
                        "Reto Urbano" -> LatLng(-12.1520, -77.0210)
                        "EcoCamino" -> LatLng(-12.0650, -77.0355)
                        else -> LatLng(-12.0464, -77.0300)
                    }
                    ruta.copy(
                        lat_destino = coords.latitude,
                        lng_destino = coords.longitude
                    )
                }
                adapter.actualizarLista(listaConCoords)
            },
            onError = { mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
