package com.cibertec.proyectoecorutasapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.databinding.ItemLogroBinding
import com.cibertec.proyectoecorutasapp.models.Logro

class LogroAdapter(private var lista: List<Logro>) :
    RecyclerView.Adapter<LogroAdapter.ViewHolder>() {

    inner class ViewHolder(val b: ItemLogroBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLogroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val logro = lista[position]
        holder.b.tvNombreLogro.text = logro.nombre
        holder.b.tvDescripcionLogro.text = logro.descripcion
        holder.b.tvPuntosLogro.text = "+${logro.puntos} pts"
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevaLista: List<Logro>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
