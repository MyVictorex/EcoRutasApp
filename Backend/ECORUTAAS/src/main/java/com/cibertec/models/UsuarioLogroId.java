package com.cibertec.models;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogroId implements Serializable {
    private Integer idUsuario;
    private Integer idLogro;
}
