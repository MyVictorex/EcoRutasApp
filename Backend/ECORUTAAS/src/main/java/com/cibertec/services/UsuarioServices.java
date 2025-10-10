package com.cibertec.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.Usuario;
import com.cibertec.repository.IRepositoryUsuario;

@Service
public class UsuarioServices {
	@Autowired
	private IRepositoryUsuario usuario;
	
	public List<Usuario> Usuarios(){
		
		return usuario.findAll();
	}
	
	
	public Usuario Nuevo(Usuario NuevoUsuario) {
		
		
		return usuario.save(NuevoUsuario);
	}
	
	 public Usuario actualizar(Integer id, Usuario datos) {
	        Optional<Usuario> optional = usuario.findById(id);
	        if (optional.isPresent()) {
	            Usuario u = optional.get();
	            u.setNombre(datos.getNombre());
	            u.setApellido(datos.getApellido());
	            u.setCorreo(datos.getCorreo());
	            u.setPassword(datos.getPassword());
	            u.setRol(datos.getRol());
	            return usuario.save(u);
	        } else {
	            return null;
	        }
	    }


	    public void eliminar(Integer id) {
	    	usuario.deleteById(id);
	    }
}
