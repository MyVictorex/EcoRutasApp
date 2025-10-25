package com.cibertec.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cibertec.models.Ruta;
import com.cibertec.repository.IRepositoryRuta;

@Service
public class RutaServices {

    @Autowired
    private IRepositoryRuta rutaRepo;

 
    public List<Ruta> listar() {
        return rutaRepo.findAll();
    }

  
    public Ruta insertar(Ruta nuevaRuta) {
        return rutaRepo.save(nuevaRuta);
    }

    public Ruta actualizar(Integer id, Ruta datos) {
        Optional<Ruta> optional = rutaRepo.findById(id);
        if (optional.isPresent()) {
            Ruta r = optional.get();
            r.setNombre(datos.getNombre());
            r.setDescripcion(datos.getDescripcion());
            r.setPunto_inicio(datos.getPunto_inicio());
            r.setPunto_fin(datos.getPunto_fin());
            r.setDistancia_km(datos.getDistancia_km());
            r.setTipo(datos.getTipo());
            r.setEstado(datos.getEstado());
            return rutaRepo.save(r);
        } else {
            return null;
        }
    }


    public void eliminar(Integer id) {
        rutaRepo.deleteById(id);
    }
}
