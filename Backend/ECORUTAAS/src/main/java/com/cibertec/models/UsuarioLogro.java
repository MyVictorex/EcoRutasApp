package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario_logro")
@IdClass(UsuarioLogroId.class)
public class UsuarioLogro {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Id
    @Column(name = "id_logro")
    private Integer idLogro;

    @Column(name = "fecha_obtencion")
    private LocalDateTime fechaObtencion = LocalDateTime.now();
}
