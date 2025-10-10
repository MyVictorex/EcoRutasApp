package com.cibertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.models.Usuario;

public interface IRepositoryUsuario extends JpaRepository<Usuario, Integer>{

}
