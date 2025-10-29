package com.cibertec.proyectoecorutasapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.proyectoecorutasapp.databinding.ActivityAlquilerBinding
import com.cibertec.proyectoecorutasapp.repository.AlquilerRepository
import com.cibertec.proyectoecorutasapp.ui.Adapter.ItemAlquilerAdapter
import com.cibertec.proyectoecorutasapp.ui.bottomsheet.BottomSheetVehiculos

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


        binding.fabNuevoAlquiler.setOnClickListener {
            BottomSheetVehiculos { vehiculoSeleccionado ->

                Toast.makeText(this, "Seleccionaste: ${vehiculoSeleccionado.tipo}", Toast.LENGTH_SHORT).show()

                // Aquí después vas a crear el registro del alquiler
                // (falta un paso y lo hacemos enseguida)

            }.show(supportFragmentManager, "BottomSheetVehiculos")
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val vehiculoId = data.getIntExtra("vehiculo_id", -1)

            if (vehiculoId != -1) {
                // Aquí viene lo bueno: registramos alquiler
                registrarNuevoAlquiler(vehiculoId)
            }
        }
    }
    private fun registrarNuevoAlquiler(vehiculoId: Int) {
        // TODO: Después aquí agregamos datos de usuario y ruta actual
        Toast.makeText(this, "🚀 Aquí se registraría alquiler para vehículo #$vehiculoId", Toast.LENGTH_SHORT).show()
    }

    private fun cargarAlquileres() {
        repo.listarAlquileres(
            onSuccess = { lista ->
                Toast.makeText(this, "Se obtuvieron ${lista.size} alquileres", Toast.LENGTH_SHORT).show()
                adapter.actualizarLista(lista)
            },
            onError = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )
    }

}
