package com.cibertec.proyectoecorutasapp.ui.Adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.proyectoecorutasapp.databinding.ItemAlquilerBinding
import com.cibertec.proyectoecorutasapp.models.Alquiler

class ItemAlquilerAdapter(
    private var lista: List<Alquiler>,
    private val onFinalizarClick: (Alquiler) -> Unit
) : RecyclerView.Adapter<ItemAlquilerAdapter.ViewHolder>() {

    inner class ViewHolder(val b: ItemAlquilerBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlquilerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = lista[position]
        holder.b.tvVehiculo.text = "Vehículo: ${a.vehiculo.tipo}"
        holder.b.tvEstado.text = "Estado: ${a.estado}"
        holder.b.tvTiempo.text = "Tiempo: ${a.fecha_inicio ?: "-"}"
        holder.b.tvCosto.text = "Costo: S/. ${a.costo ?: 0.0}"

        holder.b.btnFinalizar.setOnClickListener {
            onFinalizarClick(a)
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Alquiler>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
