import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VehiculoService } from '../../../../services/vehiculo.service';
import { Vehiculo } from '../../../../models/vehiculo';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {

  vehiculo: Vehiculo = {
    tipo: 'BICICLETA',
    codigo_qr: '',
    disponible: true,
    ubicacion_actual: '',
    latitud: 0,
    longitud: 0
  };

  editando = false;
  mensaje = '';
  cargando = false;

  constructor(
    private vehiculoService: VehiculoService,
    private route: ActivatedRoute,
    public router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.obtenerVehiculo(Number(id));
    } else {
      this.obtenerUbicacionActual();
    }
  }

  obtenerVehiculo(id: number): void {
    this.cargando = true;
    this.vehiculoService.listar().subscribe({
      next: (vehiculos) => {
        const encontrado = vehiculos.find(v => v.id_vehiculo === id);
        if (encontrado) this.vehiculo = encontrado;
        this.cargando = false;
      },
      error: () => {
        this.mensaje = '❌ Error al cargar el vehículo';
        this.cargando = false;
      }
    });
  }

  obtenerUbicacionActual(): void {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          this.vehiculo.latitud = pos.coords.latitude;
          this.vehiculo.longitud = pos.coords.longitude;
          this.vehiculo.ubicacion_actual = 'Ubicación GPS detectada';
        },
        (err) => {
          console.warn('Error al obtener la ubicación', err);
          this.vehiculo.ubicacion_actual = 'Ubicación desconocida';
        }
      );
    } else {
      console.warn('Geolocalización no soportada');
    }
  }

  guardar(): void {
    this.cargando = true;

    if (!this.editando) delete this.vehiculo.id_vehiculo;

    const request = this.editando
      ? this.vehiculoService.actualizar(this.vehiculo.id_vehiculo!, this.vehiculo)
      : this.vehiculoService.insertar(this.vehiculo);

    request.subscribe({
      next: () => {
        this.mensaje = this.editando
          ? '✅ Vehículo actualizado correctamente'
          : '✅ Vehículo registrado correctamente';
        this.cargando = false;
        setTimeout(() => this.router.navigate(['/vehiculos']), 1200);
      },
      error: (err) => {
        console.error(err);
        this.mensaje = '❌ Error al guardar el vehículo';
        this.cargando = false;
      }
    });
  }
}
