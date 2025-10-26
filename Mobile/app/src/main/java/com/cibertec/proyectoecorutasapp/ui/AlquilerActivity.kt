package com.cibertec.proyectoecorutasapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.proyectoecorutasapp.databinding.ActivityAlquilerBinding
import com.cibertec.proyectoecorutasapp.repository.AlquilerRepository
import com.cibertec.proyectoecorutasapp.ui.Adapter.ItemAlquilerAdapter

class AlquilerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlquilerBinding
    private lateinit var adapter: ItemAlquilerAdapter
    private lateinit var repo: AlquilerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlquilerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = AlquilerRepository(this)


        adapter = ItemAlquilerAdapter(emptyList()) { alquiler ->
            Toast.makeText(this, "Finalizar alquiler #${alquiler.id_alquiler}", Toast.LENGTH_SHORT).show()
        }

        binding.rvAlquileres.layoutManager = LinearLayoutManager(this)
        binding.rvAlquileres.adapter = adapter


        cargarAlquileres()
    }

    private fun cargarAlquileres() {
        repo.listarAlquileres(
            onSuccess = { lista ->
                adapter.actualizarLista(lista)
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )
    }
}
