package com.cibertec.dtos;

import java.util.List; // ✅ Import correcto
import com.cibertec.models.HistorialRuta.ModoTransporte;
import lombok.Data;

@Data
public class HistorialRutaDTO {

    private Integer idRuta;            // id de la ruta, si es ruta predefinida
    private String usuarioId;          // id del usuario que realiza la ruta
    private ModoTransporte modo;       // tipo de transporte seleccionado en el spinner
    private List<List<Double>> ruta;   // lista de coordenadas [lat, lng] de la ruta completa
    private Double distanciaRecorrida; // opcional: calculada en Android
    private Integer duracionMinutos;   // opcional: calculada en Android
}
