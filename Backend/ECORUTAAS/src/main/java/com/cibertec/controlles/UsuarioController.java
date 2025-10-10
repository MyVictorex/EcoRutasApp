package com.cibertec.controlles;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cibertec.models.Usuario;
import com.cibertec.services.UsuarioServices;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UsuarioServices usuarioServices;


    @GetMapping
    public List<Usuario> listar() {
        return usuarioServices.Usuarios();
    }


    @PostMapping
    public Usuario insertar(@RequestBody Usuario nuevoUsuario) {
        return usuarioServices.Nuevo(nuevoUsuario);
    }


    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Integer id, @RequestBody Usuario datos) {
        return usuarioServices.actualizar(id, datos);
    }

 
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        usuarioServices.eliminar(id);
    }
}

