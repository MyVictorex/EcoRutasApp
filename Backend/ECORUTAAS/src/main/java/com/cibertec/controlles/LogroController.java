package com.cibertec.controlles;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Logro;
import com.cibertec.models.UsuarioLogro;
import com.cibertec.services.LogroServices;
import com.cibertec.services.UsuarioLogroService;

@RestController
@RequestMapping("/api/logros")
@CrossOrigin(origins = "http://localhost:4200")
public class LogroController {

    @Autowired
    private LogroServices logroServices;
    
    private UsuarioLogroService service;

    @GetMapping("/{idUsuario}")
    public List<UsuarioLogro> listarPorUsuario(@PathVariable Integer idUsuario) {
        return service.listarPorUsuario(idUsuario);
    }

    @PostMapping("/otorgar")
    public Map<String, Object> otorgar(@RequestBody Map<String, Integer> body) {
        Integer idUsuario = body.get("idUsuario");
        Integer idLogro = body.get("idLogro");
        boolean otorgado = service.otorgarLogro(idUsuario, idLogro);

        return Map.of(
            "otorgado", otorgado,
            "mensaje", otorgado ? "🎉 Logro otorgado correctamente" : "El usuario ya tenía este logro"
        );
    }

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
