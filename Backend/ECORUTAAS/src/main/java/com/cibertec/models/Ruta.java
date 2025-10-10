package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruta")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_ruta;

    private String nombre;
    private String descripcion;
    private String punto_inicio;
    private String punto_fin;
    private Double distancia_km;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVA;

    private LocalDateTime fecha_creacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "ruta")
    private List<Alquiler> alquileres;

    @OneToMany(mappedBy = "ruta")
    private List<HistorialRuta> historialRutas;

    public enum Tipo { BICICLETA, SCOOTER, CAMINATA, CARPOOL }
    public enum Estado { ACTIVA, INACTIVA }
}
