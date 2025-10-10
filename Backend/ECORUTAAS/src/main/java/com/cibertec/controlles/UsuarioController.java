package com.cibertec.controlles;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cibertec.Utils.JwtUtil;
import com.cibertec.dtos.AutenticacionFiltro;
import com.cibertec.models.Usuario;
import com.cibertec.services.UsuarioServices;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

	@Autowired
    private JwtUtil jwU;
	
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
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AutenticacionFiltro filtro) {
        try {
            
            Usuario usuarioEncontrado = usuarioServices.autenticar(filtro);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Correo o contraseña incorrectos");
            }

           
            String token = jwU.generarToken(usuarioEncontrado);

           
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("usuario", usuarioEncontrado);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al iniciar sesión: " + e.getMessage());
        }
    }

    
}

