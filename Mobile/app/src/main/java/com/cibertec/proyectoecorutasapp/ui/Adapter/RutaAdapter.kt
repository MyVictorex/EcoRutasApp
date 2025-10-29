package com.cibertec.proyectoecorutasapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.databinding.ItemRutaBinding
import com.cibertec.proyectoecorutasapp.models.Ruta
import com.cibertec.proyectoecorutasapp.ui.MainActivity

class RutaAdapter(
    private var lista: List<Ruta>
) : RecyclerView.Adapter<RutaAdapter.ViewHolder>() {

    private var onItemClickListener: ((Ruta) -> Unit)? = null

    inner class ViewHolder(val b: ItemRutaBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(ruta: Ruta) {
            b.tvNombreRuta.text = ruta.nombre
            b.tvTipoRuta.text = "Tipo: ${ruta.tipo}"
            b.tvDistanciaRuta.text = "Distancia: ${ruta.distancia_km ?: 0.0} km"
            b.tvEstadoRuta.text = "Estado: ${ruta.estado}"

            b.root.setOnClickListener {
                onItemClickListener?.invoke(ruta)
            }

            b.btnIniciarRuta.setOnClickListener {
                if (ruta.lat_destino != null && ruta.lng_destino != null) {
                    val intent = Intent(b.root.context, MainActivity::class.java)
                    intent.putExtra("lat_destino", ruta.lat_destino)
                    intent.putExtra("lng_destino", ruta.lng_destino)
                    b.root.context.startActivity(intent)
                } else {
                    Toast.makeText(b.root.context, "Coordenadas de destino no definidas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRutaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Ruta>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Ruta) -> Unit) {
        onItemClickListener = listener
    }
}


