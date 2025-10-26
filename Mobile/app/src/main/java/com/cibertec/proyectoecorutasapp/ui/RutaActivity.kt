package com.cibertec.proyectoecorutasapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.proyectoecorutasapp.databinding.AcitivityRutaBinding
import com.cibertec.proyectoecorutasapp.repository.RutaRepository
import com.cibertec.proyectoecorutasapp.ui.adapter.RutaAdapter

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

        // Cargar rutas
        cargarRutas()
    }

    private fun cargarRutas() {
        repository.listarRutas(
            onSuccess = { lista ->
                adapter.actualizarLista(lista)
            },
            onError = { mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
