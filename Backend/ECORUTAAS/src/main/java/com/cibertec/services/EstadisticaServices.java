package com.cibertec.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.Estadistica;
import com.cibertec.repository.IRepositoryAlquiler;
import com.cibertec.repository.IRepositoryEstadistica;
import com.cibertec.repository.IRepositoryHistorialRuta;
import com.cibertec.repository.IRepositoryRuta;
import com.cibertec.repository.IRepositoryUsuario;

@Service
public class EstadisticaServices {

    @Autowired
    private IRepositoryEstadistica estadisticaRepo;

    @Autowired
    private IRepositoryUsuario usuarioRepo;

    @Autowired
    private IRepositoryRuta rutaRepo;

    @Autowired
    private IRepositoryAlquiler alquilerRepo;

    @Autowired
    private IRepositoryHistorialRuta historialRepo;

  
    public List<Estadistica> listar() {
        return estadisticaRepo.findAll();
    }


    public Estadistica generarEstadistica() {
        Estadistica e = new Estadistica();
        e.setFecha(LocalDate.now());
        e.setTotal_usuarios((int) usuarioRepo.count());
        e.setTotal_rutas((int) rutaRepo.count());
        e.setTotal_alquileres((int) alquilerRepo.count());
        e.setCo2_ahorrado(historialRepo.sumCo2());
        return estadisticaRepo.save(e);
    }
}
