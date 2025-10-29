package com.cibertec.proyectoecorutasapp.ui.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.R
import com.cibertec.proyectoecorutasapp.models.HistorialRuta

class HistorialRutaAdapter(private var lista: List<HistorialRuta>) :
    RecyclerView.Adapter<HistorialRutaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRuta: TextView = view.findViewById(R.id.txtNombreRuta)
        val txtFecha: TextView = view.findViewById(R.id.txtFechaRuta)
        val txtDistancia: TextView = view.findViewById(R.id.txtDistanciaRuta)
        val txtCo2: TextView = view.findViewById(R.id.txtCo2Ruta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_ruta, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.txtRuta.text = item.ruta.nombre
        holder.txtFecha.text = "Inicio: ${item.fecha_inicio}"
        holder.txtDistancia.text = "Distancia: ${item.distancia_recorrida} km"
        holder.txtCo2.text = "CO₂ ahorrado: ${item.co2_ahorrado} kg"
    }

    fun actualizarLista(nuevaLista: List<HistorialRuta>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
