package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_vehiculo;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Column(unique = true)
    private String codigo_qr;

    private Boolean disponible = true;
    private String ubicacion_actual;
    private LocalDateTime fecha_registro = LocalDateTime.now();

    @OneToMany(mappedBy = "vehiculo")
    private List<Alquiler> alquileres;

    public enum Tipo { BICICLETA, SCOOTER }
}
