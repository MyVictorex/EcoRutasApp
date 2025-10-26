package com.cibertec.proyectoecorutasapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.databinding.ItemRutaBinding
import com.cibertec.proyectoecorutasapp.models.Ruta

class RutaAdapter(
    private var lista: List<Ruta>
) : RecyclerView.Adapter<RutaAdapter.ViewHolder>() {

    inner class ViewHolder(val b: ItemRutaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRutaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = lista[position]
        holder.b.tvNombreRuta.text = r.nombre
        holder.b.tvTipoRuta.text = "Tipo: ${r.tipo}"
        holder.b.tvDistanciaRuta.text = "Distancia: ${r.distancia_km ?: 0.0} km"
        holder.b.tvEstadoRuta.text = "Estado: ${r.estado}"
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Ruta>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
