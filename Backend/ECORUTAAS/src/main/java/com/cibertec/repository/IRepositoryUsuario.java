package com.cibertec.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.models.Usuario;

public interface IRepositoryUsuario extends JpaRepository<Usuario, Integer>{

	 Usuario findByCorreoAndPassword(String correo, String password); 

	 Optional<Usuario> findByCorreo(String correo);
}
