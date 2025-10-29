package com.cibertec.proyectoecorutasapp.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.R
import com.cibertec.proyectoecorutasapp.repository.HistorialRutaRepository
import com.cibertec.proyectoecorutasapp.ui.historial.HistorialRutaAdapter

class HistorialRutaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: HistorialRutaAdapter
    private lateinit var repository: HistorialRutaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_ruta)

        recyclerView = findViewById(R.id.recyclerHistorial)
        progressBar = findViewById(R.id.progressBarHistorial)

        repository = HistorialRutaRepository(this)
        adapter = HistorialRutaAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        cargarHistorial()
    }

    private fun cargarHistorial() {
        progressBar.visibility = View.VISIBLE

        repository.listarHistorial(
            onSuccess = { lista ->
                progressBar.visibility = View.GONE
                adapter.actualizarLista(lista)
            },
            onError = { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
