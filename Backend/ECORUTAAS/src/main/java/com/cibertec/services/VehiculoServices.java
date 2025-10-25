package com.cibertec.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.Vehiculo;
import com.cibertec.repository.IRepositoryVehiculo;

@Service
public class VehiculoServices {
	@Autowired
	private IRepositoryVehiculo Carrito;
	
	public List<Vehiculo> Carros(){
		
		
		return Carrito.findAll();
	}
	
	public Vehiculo Insertar(Vehiculo NuevoCarrito)
	{
		
		return Carrito.save(NuevoCarrito);
	}
	
	public Vehiculo Cambios(Integer id, Vehiculo datos) {
        Optional<Vehiculo> optional = Carrito.findById(id);
        if (optional.isPresent()) {
            Vehiculo v = optional.get();
            v.setTipo(datos.getTipo());
            v.setCodigo_qr(datos.getCodigo_qr());
            v.setDisponible(datos.getDisponible());
            v.setUbicacion_actual(datos.getUbicacion_actual());
            v.setFecha_registro(datos.getFecha_registro());
            return Carrito.save(v);
        } else {
            return null;
        }
    }


    public void Descartado(Integer id) {
    	Carrito.deleteById(id);
    }
}
