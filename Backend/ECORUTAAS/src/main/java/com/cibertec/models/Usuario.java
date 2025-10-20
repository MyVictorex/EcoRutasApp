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
@Table(name = "usuario")
@ToString(exclude = {"rutas", "alquileres", "historialRutas", "logros"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;

    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.USUARIO;

    private LocalDateTime fecha_registro = LocalDateTime.now();

   
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ruta> rutas;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Alquiler> alquileres;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HistorialRuta> historialRutas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_logro",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_logro")
    )
    @JsonIgnore
    private List<Logro> logros;


    public enum Rol { USUARIO, ADMIN }
}
