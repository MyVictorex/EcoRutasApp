package com.cibertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.models.Vehiculo;

public interface IRepositoryVehiculo extends JpaRepository<Vehiculo, Integer> {

}
