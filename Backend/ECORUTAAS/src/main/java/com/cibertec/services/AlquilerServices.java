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


    public Alquiler finalizarAlquiler(Integer id, Alquiler datos) {
        Optional<Alquiler> alquilerExistente = alquilerRepo.findById(id);
        if (alquilerExistente.isPresent()) {
            Alquiler a = alquilerExistente.get();
            a.setFecha_fin(datos.getFecha_fin());
            a.setCosto(datos.getCosto());
            a.setEstado(Alquiler.Estado.FINALIZADO);
            return alquilerRepo.save(a);
        } else {
            return null;
        }
    }
}
