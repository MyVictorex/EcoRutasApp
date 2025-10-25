package com.cibertec.controlles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Logro;
import com.cibertec.services.LogroServices;

@RestController
@RequestMapping("/api/logros")
@CrossOrigin(origins = "http://localhost:4200")
public class LogroController {

    @Autowired
    private LogroServices logroServices;

    @GetMapping
    public List<Logro> listar() {
        return logroServices.Listar();
    }

    @PostMapping
    public Logro insertar(@RequestBody Logro nuevoLogro) {
        return logroServices.Insertar(nuevoLogro);
    }

    @PutMapping("/{id}")
    public Logro actualizar(@PathVariable Integer id, @RequestBody Logro datos) {
        return logroServices.actualizar(id, datos);
    }
}
