package com.cibertec.controlles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dtos.HistorialRutaDTO;
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


	    @PostMapping("/ruta-libre")
	    public ResponseEntity<Integer> registrarRutaLibre(@RequestBody HistorialRutaDTO dto) {

	        // 1️⃣ Inicio de la ruta
	        List<Double> inicio = dto.getRuta().get(0);
	        String puntoInicio = inicio.get(0) + "," + inicio.get(1);

	        // 2️⃣ Fin de la ruta
	        List<Double> ultimo = dto.getRuta().get(dto.getRuta().size() - 1);
	        String puntoFin = ultimo.get(0) + "," + ultimo.get(1);

	        // 3️⃣ Llamada al service que ejecuta el procedure
	        Integer idHistorial = histori.registrarHistorialLibre(
	                Integer.parseInt(dto.getUsuarioId()),
	                "Ruta Libre " + System.currentTimeMillis(),
	                puntoInicio,
	                puntoFin,
	                dto.getDistanciaRecorrida(),
	                dto.getModo().name(),
	                dto.getDuracionMinutos(),
	                0.0 // CO2, opcional o calculado
	        );

	        return ResponseEntity.ok(idHistorial);
	    }

}

