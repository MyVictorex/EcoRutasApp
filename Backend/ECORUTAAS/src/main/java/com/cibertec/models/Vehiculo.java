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
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_vehiculo;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private String codigo_qr;
    private Boolean disponible = true;
    private String ubicacion_actual;
    private LocalDateTime fecha_registro = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "vehiculo")
    private List<Alquiler> alquileres;

    public enum Tipo { 
        BICICLETA, 
        SCOOTER, 
        CAMINATA,    
        CARPOOL     
    }

}
