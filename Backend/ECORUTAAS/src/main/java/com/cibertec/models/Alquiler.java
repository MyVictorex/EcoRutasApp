package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alquiler")
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_alquiler;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo")
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    private LocalDateTime fecha_inicio = LocalDateTime.now();
    private LocalDateTime fecha_fin;
    private Double costo;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.EN_CURSO;

    public enum Estado { EN_CURSO, FINALIZADO }
}
