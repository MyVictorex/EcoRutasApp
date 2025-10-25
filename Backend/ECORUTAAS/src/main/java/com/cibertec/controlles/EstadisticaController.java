package com.cibertec.controlles;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Estadistica;
import com.cibertec.services.EstadisticaServices;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "http://localhost:4200")
public class EstadisticaController {

    @Autowired
    private EstadisticaServices estadisticaServices;


    @GetMapping
    public List<Estadistica> listar() {
        return estadisticaServices.listar();
    }


    @PostMapping
    public Estadistica generar() {
        return estadisticaServices.generarEstadistica();
    }
}
