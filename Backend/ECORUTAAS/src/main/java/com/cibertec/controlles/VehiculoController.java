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

    // 🔹 Listar vehículos
    @GetMapping
    public List<Vehiculo> listar() {
        return vehiculoServices.Carros();
    }

    // 🔹 Registrar vehículo
    @PostMapping
    public Vehiculo insertar(@RequestBody Vehiculo nuevoVehiculo) {
        return vehiculoServices.Insertar(nuevoVehiculo);
    }

    // 🔹 Actualizar vehículo
    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Integer id, @RequestBody Vehiculo datos) {
        return vehiculoServices.Cambios(id, datos);
    }

    // 🔹 Eliminar vehículo
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        vehiculoServices.Descartado(id);
    }
}

