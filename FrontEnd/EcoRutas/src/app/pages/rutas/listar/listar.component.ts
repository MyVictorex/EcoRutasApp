import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RutaService } from '../../../../services/ruta.service';
import { Ruta } from '../../../../models/ruta';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-listar',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './listar.component.html',
  styleUrl: './listar.component.scss'
})
export class ListarComponent implements OnInit {

  rutas: Ruta[] = [];
  cargando = true;
  errorMsg = '';

  constructor(private rutaService: RutaService) {}

  ngOnInit(): void {
    this.obtenerRutas();
  }

  obtenerRutas(): void {
    this.rutaService.listar().subscribe({
      next: (data) => {
        this.rutas = data;
        this.cargando = false;
      },
      error: (err: any) => {
        console.error(err);
        this.errorMsg = 'Error al cargar las rutas.';
        this.cargando = false;
      }
    });
  }

  eliminarRuta(id?: number): void {
  if (!id) {
    alert('ID de ruta no válido ❌');
    return;
  }

  if (confirm('¿Seguro que deseas eliminar esta ruta?')) {
    this.rutaService.eliminar(id).subscribe({
      next: () => {
        this.rutas = this.rutas.filter(r => r.id_ruta !== id);
        alert('Ruta eliminada correctamente ✅');
      },
      error: (err: any) => {
        console.error(err);
        alert('Error al eliminar la ruta ❌');
      }
    });
  }
}
}
