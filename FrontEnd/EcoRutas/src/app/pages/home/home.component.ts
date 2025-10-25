import { Component, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';
import { VehiculoService } from '../../../services/vehiculo.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements AfterViewInit {
  private map!: L.Map;
  private userMarker!: L.Marker;

  constructor(private vehiculoService: VehiculoService) {}

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    // Obtener ubicación del usuario
    navigator.geolocation.getCurrentPosition((pos) => {
      const { latitude, longitude } = pos.coords;

    this.map = L.map('map').setView([latitude, longitude], 15);
setTimeout(() => this.map.invalidateSize(), 200); 


      // Capa base
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '© OpenStreetMap'
      }).addTo(this.map);

      // Marcador del usuario
      this.userMarker = L.marker([latitude, longitude]).addTo(this.map);
      this.userMarker.bindPopup('📍 Tu ubicación actual').openPopup();

      // Cargar vehículos cercanos
      this.cargarVehiculosCercanos(latitude, longitude);
    });
  }

  private cargarVehiculosCercanos(lat: number, lng: number): void {
    this.vehiculoService.listar().subscribe((vehiculos) => {
      vehiculos.forEach(v => {
        // Aquí podrías calcular distancia entre usuario y vehículo
        if (v.ubicacion_actual) {
          // Simulando coordenadas de prueba (puedes guardarlas en tu BD luego)
          const randomLat = lat + (Math.random() - 0.5) * 0.01;
          const randomLng = lng + (Math.random() - 0.5) * 0.01;

          const icono = L.icon({
            iconUrl:
              v.tipo === 'BICICLETA' ? 'assets/icons/bici.png' :
              v.tipo === 'SCOOTER' ? 'assets/icons/scooter.png' :
              v.tipo === 'MONOPATIN_ELECTRICO' ? 'assets/icons/monopatin.png' :
              v.tipo === 'SEGWAY' ? 'assets/icons/segway.png' :
              'assets/icons/carpool.png',
            iconSize: [40, 40]
          });

         if (v.latitud && v.longitud) {
  L.marker([v.latitud, v.longitud], { icon: icono })
    .addTo(this.map)
    .bindPopup(`
      <b>${v.tipo}</b><br>
      ${v.disponible ? '✅ Disponible' : '❌ Ocupado'}<br>
      <button onclick="alert('Alquilar ${v.tipo}')">Alquilar</button>
    `);
}

        }
      });
    });
  }
}
