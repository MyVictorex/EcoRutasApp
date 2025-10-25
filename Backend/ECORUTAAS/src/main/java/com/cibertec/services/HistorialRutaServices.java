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
}
