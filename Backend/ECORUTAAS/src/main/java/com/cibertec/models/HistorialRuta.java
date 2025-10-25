package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historial_ruta")
public class HistorialRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_historial;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;

    private LocalDateTime fecha_inicio = LocalDateTime.now();
    private LocalDateTime fecha_fin;
    private Double distancia_recorrida;
    private Integer duracion_minutos;

    @Enumerated(EnumType.STRING)
    private ModoTransporte modo_transporte;

    private Double co2_ahorrado;

    public enum ModoTransporte { BICICLETA, SCOOTER, MONOPATIN_ELECTRICO, CARPOOL ,SEGWAY}
}

