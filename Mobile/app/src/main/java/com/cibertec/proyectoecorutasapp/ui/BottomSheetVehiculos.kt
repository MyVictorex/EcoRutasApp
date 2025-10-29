package com.cibertec.proyectoecorutasapp.ui.bottomsheet

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.proyectoecorutasapp.databinding.BottomsheetVehiculosBinding
import com.cibertec.proyectoecorutasapp.models.Vehiculo
import com.cibertec.proyectoecorutasapp.repository.VehiculoRepository
import com.cibertec.proyectoecorutasapp.ui.Adapter.ItemVehiculoAdapter

class BottomSheetVehiculos(
    private val onVehiculoSeleccionado: (Vehiculo) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetVehiculosBinding? = null
    private val binding get() = _binding!!

    private lateinit var repo: VehiculoRepository
    private lateinit var adapter: ItemVehiculoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetVehiculosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repo = VehiculoRepository(requireContext())

        adapter = ItemVehiculoAdapter(emptyList()) { vehiculo ->
            onVehiculoSeleccionado(vehiculo)
            dismiss() // cerramos el modal
        }

        binding.rvVehiculos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVehiculos.adapter = adapter

        repo.listarVehiculos(
            onSuccess = { lista -> adapter.actualizar(lista) },
            onError = { }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
