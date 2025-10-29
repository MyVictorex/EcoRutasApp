package com.cibertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.cibertec.models.HistorialRuta;

public interface IRepositoryHistorialRuta extends JpaRepository<HistorialRuta, Integer> {
	@Query("SELECT COALESCE(SUM(h.co2_ahorrado), 0) FROM HistorialRuta h")
    Double sumCo2();

    // ⚡ Nuevo: Ejecuta el procedimiento almacenado
	@Query(value = "CALL sp_registrar_historial_libre(:idUsuario, :nombreRuta, :puntoInicio, :puntoFin, :distancia, :tipo, :duracion, :co2)", nativeQuery = true)
    Integer registrarHistorialLibre(
            @Param("idUsuario") Integer idUsuario,
            @Param("nombreRuta") String nombreRuta,
            @Param("puntoInicio") String puntoInicio,
            @Param("puntoFin") String puntoFin,
            @Param("distancia") Double distancia,
            @Param("tipo") String tipo,
            @Param("duracion") Integer duracion,
            @Param("co2") Double co2
    );

}
