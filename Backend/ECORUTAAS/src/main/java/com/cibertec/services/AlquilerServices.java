package com.cibertec.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.Alquiler;
import com.cibertec.repository.IRepositoryAlquiler;

@Service
public class AlquilerServices {

    @Autowired
    private IRepositoryAlquiler alquilerRepo;


    public List<Alquiler> listado() {
        return alquilerRepo.findAll();
    }

  
    public Alquiler registrar(Alquiler nuevoAlquiler) {
        return alquilerRepo.save(nuevoAlquiler);
    }


    public Alquiler actualizar(Integer id, Alquiler datos) {
        Alquiler a = alquilerRepo.findById(id).orElseThrow(() -> new RuntimeException("No encontrado"));
        a.setFecha_fin(datos.getFecha_fin());
        a.setCosto(datos.getCosto());
        a.setEstado(datos.getEstado());
        a.setVehiculo(datos.getVehiculo());
        a.setRuta(datos.getRuta());
        a.setUsuario(datos.getUsuario());
        return alquilerRepo.save(a);
    }
}
