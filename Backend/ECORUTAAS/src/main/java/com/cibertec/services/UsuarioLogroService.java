package com.cibertec.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cibertec.models.UsuarioLogro;
import com.cibertec.models.UsuarioLogroId;
import com.cibertec.repository.IRepositoryUsuarioLogro;

import java.util.Optional;
import java.util.List;

@Service
public class UsuarioLogroService {

    @Autowired
    private IRepositoryUsuarioLogro repo;

    public List<UsuarioLogro> listarPorUsuario(Integer idUsuario) {
        return repo.findByIdUsuario(idUsuario);
    }

    public boolean otorgarLogro(Integer idUsuario, Integer idLogro) {
        UsuarioLogroId id = new UsuarioLogroId(idUsuario, idLogro);
        Optional<UsuarioLogro> existente = repo.findById(id);

        if (existente.isEmpty()) {
            repo.save(new UsuarioLogro(idUsuario, idLogro, null));
            return true; // Logro otorgado
        }
        return false; // Ya lo tenía
    }
}
