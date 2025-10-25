package com.cibertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cibertec.models.HistorialRuta;

public interface IRepositoryHistorialRuta extends JpaRepository<HistorialRuta, Integer> {
	@Query("SELECT COALESCE(SUM(h.co2_ahorrado), 0) FROM HistorialRuta h")
	Double sumCo2();

}
