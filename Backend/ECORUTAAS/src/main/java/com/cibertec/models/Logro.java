package com.cibertec.models;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logro")
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_logro;

    private String nombre;
    private String descripcion;
    private Integer puntos;

    @ManyToMany(mappedBy = "logros")
    private List<Usuario> usuarios;
}

