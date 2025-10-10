package com.cibertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.cibertec.models.Alquiler;

public interface IRepositoryAlquiler extends JpaRepository<Alquiler, Integer> {

}
