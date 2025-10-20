import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { VehiculoService } from '../../../../services/vehiculo.service';
import { Vehiculo } from '../../../../models/vehiculo';

@Component({
  selector: 'app-listar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './listar.component.html',
  styleUrl: './listar.component.scss'
})
export class ListarComponent implements OnInit {

  vehiculos: Vehiculo[] = [];
  cargando = false;
  errorMsg = '';

  constructor(private vehiculoService: VehiculoService, private router: Router) {}

  ngOnInit(): void {
    this.obtenerVehiculos();
  }

  obtenerVehiculos(): void {
    this.cargando = true;
    this.vehiculoService.listar().subscribe({
      next: (data) => {
        this.vehiculos = data;
        this.cargando = false;
      },
      error: () => {
        this.errorMsg = 'Error al cargar los vehículos ❌';
        this.cargando = false;
      }
    });
  }

  eliminarVehiculo(id: number | undefined): void {
    if (!id) return;

    const confirmar = confirm('¿Seguro que deseas eliminar este vehículo? 🚗❌');
    if (confirmar) {
      this.vehiculoService.eliminar(id).subscribe({
        next: () => {
          this.vehiculos = this.vehiculos.filter(v => v.id_vehiculo !== id);
        },
        error: () => {
          this.errorMsg = 'Error al eliminar el vehículo ❌';
        }
      });
    }
  }

  editarVehiculo(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/vehiculos/editar', id]);
    }
  }

  nuevoVehiculo(): void {
    this.router.navigate(['/vehiculos/nuevo']);
  }
}
