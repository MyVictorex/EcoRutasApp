package com.cibertec.proyectoecorutasapp.ui.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.databinding.ItemVehiculoBinding
import com.cibertec.proyectoecorutasapp.models.Vehiculo

class ItemVehiculoAdapter(
    private var lista: List<Vehiculo>,
    private val onSelect: (Vehiculo) -> Unit
) : RecyclerView.Adapter<ItemVehiculoAdapter.ViewHolder>() {

    inner class ViewHolder(val b: ItemVehiculoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVehiculoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = lista[position]

        holder.b.tvTipo.text = v.tipo.name
        holder.b.tvUbicacion.text = v.ubicacion_actual ?: "Sin ubicación"

        holder.b.root.setOnClickListener { onSelect(v) }
    }

    override fun getItemCount() = lista.size

    fun actualizar(nuevaLista: List<Vehiculo>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
