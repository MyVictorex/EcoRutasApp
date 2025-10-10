package com.cibertec.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
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

    @OneToMany(mappedBy = "usuario")
    private List<Ruta> rutas;

    @OneToMany(mappedBy = "usuario")
    private List<Alquiler> alquileres;

    @OneToMany(mappedBy = "usuario")
    private List<HistorialRuta> historialRutas;

    @ManyToMany
    @JoinTable(
        name = "usuario_logro",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_logro")
    )
    private List<Logro> logros;

    public enum Rol { USUARIO, ADMIN }
}
