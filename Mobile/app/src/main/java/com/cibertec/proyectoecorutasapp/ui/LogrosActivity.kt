package com.cibertec.proyectoecorutasapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.proyectoecorutasapp.databinding.AcitivityLogrosBinding
import com.cibertec.proyectoecorutasapp.repository.LogroRepository
import com.cibertec.proyectoecorutasapp.ui.adapter.LogroAdapter

class LogrosActivity : AppCompatActivity() {

    private lateinit var b: AcitivityLogrosBinding
    private lateinit var repository: LogroRepository
    private lateinit var adapter: LogroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = AcitivityLogrosBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Toolbar
        setSupportActionBar(b.toolbarLogros)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbarLogros.setNavigationOnClickListener { finish() }

        // Repositorio y Adapter
        repository = LogroRepository(this)
        adapter = LogroAdapter(emptyList())

        // Configurar RecyclerView
        b.rvLogros.layoutManager = LinearLayoutManager(this)
        b.rvLogros.adapter = adapter

        // Cargar logros
        cargarLogros()
    }

    private fun cargarLogros() {
        repository.listarLogros(
            onSuccess = {
                adapter.actualizar(it)
            },
            onError = {
                Toast.makeText(this, "Error al cargar logros: $it", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
