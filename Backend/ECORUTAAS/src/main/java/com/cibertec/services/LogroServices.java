package com.cibertec.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.Logro;
import com.cibertec.repository.IRepositoryLogro;

@Service
public class LogroServices {

	 @Autowired
	 private IRepositoryLogro logro;
	 
	 public List<Logro> Listar(){
		 
		 return logro.findAll();
		 
	 }
	 
	 
	 public Logro Insertar(Logro nuevologro) {
		 
		 return logro.save(nuevologro);
	 }
	 
	 public Logro actualizar(Integer id, Logro datosActualizados) {
	        Optional<Logro> optional = logro.findById(id);
	        if (optional.isPresent()) {
	            Logro l = optional.get();
	            l.setNombre(datosActualizados.getNombre());
	            l.setDescripcion(datosActualizados.getDescripcion());
	            l.setPuntos(datosActualizados.getPuntos());
	            return logro.save(l);
	        } else {
	            return null;
	        }
	    }
	 
}
