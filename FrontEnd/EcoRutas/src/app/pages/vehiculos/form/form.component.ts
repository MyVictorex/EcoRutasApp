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
  styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {

  vehiculo: Vehiculo = {
    id_vehiculo: 0,
    tipo: 'BICICLETA',
    codigo_qr: '',
    disponible: true,
    ubicacion_actual: '',
    fecha_registro: ''
  };

  editando = false;
  mensaje = '';
  cargando = false;

  constructor(
    private vehiculoService: VehiculoService,
    private route: ActivatedRoute,
    public  router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.obtenerVehiculo(Number(id));
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
        this.mensaje = 'Error al cargar el vehículo ❌';
        this.cargando = false;
      }
    });
  }

  guardar(): void {
    this.cargando = true;

    if (this.editando && this.vehiculo.id_vehiculo) {
      this.vehiculoService.actualizar(this.vehiculo.id_vehiculo, this.vehiculo).subscribe({
        next: () => {
          this.mensaje = 'Vehículo actualizado correctamente ✅';
          this.cargando = false;
          setTimeout(() => this.router.navigate(['/vehiculos']), 1200);
        },
        error: () => {
          this.mensaje = 'Error al actualizar el vehículo ❌';
          this.cargando = false;
        }
      });
    } else {
      this.vehiculoService.insertar(this.vehiculo).subscribe({
        next: () => {
          this.mensaje = 'Vehículo registrado correctamente ✅';
          this.cargando = false;
          setTimeout(() => this.router.navigate(['/vehiculos']), 1200);
        },
        error: () => {
          this.mensaje = 'Error al registrar el vehículo ❌';
          this.cargando = false;
        }
      });
    }
  }
}
