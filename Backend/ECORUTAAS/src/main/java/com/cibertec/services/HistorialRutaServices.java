package com.cibertec.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.HistorialRuta;
import com.cibertec.repository.IRepositoryHistorialRuta;
@Service
public class HistorialRutaServices {

	@Autowired
	private IRepositoryHistorialRuta historial;
	
	
	
	
	public List<HistorialRuta> listadoHistorial(){
		
		return historial.findAll();
	
		
	}
	
	
	 public HistorialRuta registrar(HistorialRuta Nuevo) {
	        return historial.save(Nuevo);
	    }
	 
	 public Integer registrarHistorialLibre(
	            Integer idUsuario,
	            String nombreRuta,
	            String puntoInicio,
	            String puntoFin,
	            Double distancia,
	            String tipo,
	            Integer duracion,
	            Double co2) {

	        return historial.registrarHistorialLibre(idUsuario, nombreRuta, puntoInicio, puntoFin, distancia, tipo, duracion, co2);
	    }
}
