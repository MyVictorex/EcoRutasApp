package com.cibertec.models;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estadistica")
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_estadistica;

    private LocalDate fecha;
    private Integer total_usuarios;
    private Integer total_rutas;
    private Integer total_alquileres;
    private Double co2_ahorrado;
}

