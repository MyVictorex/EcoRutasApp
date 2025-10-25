package com.cibertec.controlles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.models.HistorialRuta;
import com.cibertec.services.HistorialRutaServices;

@RestController
@RequestMapping("/api/Historial")
@CrossOrigin(origins = "http://localhost:4200")
public class HitorialRutaController {

	 @Autowired
	 private HistorialRutaServices histori;
	 
	 

	    @GetMapping
	    public List<HistorialRuta> listar() {
	        return histori.listadoHistorial();
	    }


	    @PostMapping
	    public HistorialRuta registrar(@RequestBody HistorialRuta Nuevo) {
	        return histori.registrar(Nuevo);
	    }

	
}
