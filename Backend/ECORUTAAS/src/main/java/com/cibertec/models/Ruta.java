package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Estado estado = Estado.activa;

    private LocalDateTime fecha_creacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // 🔥 Ignora relaciones que causan recursión
    @JsonIgnore
    @OneToMany(mappedBy = "ruta")
    private List<Alquiler> alquileres;

    @JsonIgnore
    @OneToMany(mappedBy = "ruta")
    private List<HistorialRuta> historialRutas;

    public enum Tipo { BICICLETA, SCOOTER, MONOPATIN_ELECTRICO, CARPOOL,SEGWAY }
    public enum Estado { activa, inactiva }
    @PrePersist
    protected void onCreate() {
        this.fecha_creacion = LocalDateTime.now();
    }

     
}
