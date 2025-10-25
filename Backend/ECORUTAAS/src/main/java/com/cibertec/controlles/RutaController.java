package com.cibertec.controlles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Ruta;
import com.cibertec.services.RutaServices;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin(origins = "http://localhost:4200")
public class RutaController {

    @Autowired
    private RutaServices rutaServices;


    @GetMapping
    public List<Ruta> Listado() {
        return rutaServices.listar();
    }


    @PostMapping
    public Ruta Insertarrutaa(@RequestBody Ruta nuevaRuta) {
        return rutaServices.insertar(nuevaRuta);
    }


    @PutMapping("/{id}")
    public Ruta Actualizar(@PathVariable Integer id, @RequestBody Ruta datos) {
        return rutaServices.actualizar(id, datos);
    }


    @DeleteMapping("/{id}")
    public void Eliminar(@PathVariable Integer id) {
        rutaServices.eliminar(id);
    }
}
