package com.cibertec.controlles;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Vehiculo;
import com.cibertec.services.VehiculoServices;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "http://localhost:4200")
public class VehiculoController {

    @Autowired
    private VehiculoServices vehiculoServices;

    @GetMapping
    public List<Vehiculo> listar() {
        return vehiculoServices.Carros();
    }


    @PostMapping
    public Vehiculo insertar(@RequestBody Vehiculo nuevoVehiculo) {
        return vehiculoServices.Insertar(nuevoVehiculo);
    }


    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Integer id, @RequestBody Vehiculo datos) {
        return vehiculoServices.Cambios(id, datos);
    }


    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        vehiculoServices.Descartado(id);
    }
    
    
    @GetMapping("/disponibles")
    public List<Vehiculo> disponibles() {
        return vehiculoServices.disponibles();
    }


}

