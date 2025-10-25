package com.cibertec.controlles;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Alquiler;
import com.cibertec.services.AlquilerServices;

@RestController
@RequestMapping("/api/alquileres")
@CrossOrigin(origins = "http://localhost:4200")
public class AlquilerController {

    @Autowired
    private AlquilerServices alquilerServices;

    @GetMapping
    public List<Alquiler> listar() {
        return alquilerServices.listado();
    }


    @PostMapping
    public Alquiler registrar(@RequestBody Alquiler nuevoAlquiler) {
        
        nuevoAlquiler.setId_alquiler(null);
        return alquilerServices.registrar(nuevoAlquiler);
    }


    @PutMapping("/{id}")
    public Alquiler actualizar(@PathVariable Integer id, @RequestBody Alquiler datos) {
        return alquilerServices.actualizar(id, datos);
    }
}

