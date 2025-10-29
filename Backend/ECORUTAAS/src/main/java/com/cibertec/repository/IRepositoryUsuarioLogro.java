package com.cibertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cibertec.models.UsuarioLogro;
import com.cibertec.models.UsuarioLogroId;
import java.util.List;

@Repository
public interface IRepositoryUsuarioLogro extends JpaRepository<UsuarioLogro, UsuarioLogroId> {
    List<UsuarioLogro> findByIdUsuario(Integer idUsuario);
}
