import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlquilerService } from '../../../../services/alquiler.service';
import { Alquiler } from '../../../../models/alquiler';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-listar',
  standalone: true, 
  imports: [CommonModule,RouterModule],
  templateUrl: './listar.component.html',
  styleUrls: ['./listar.component.scss']
})
export class ListarComponent implements OnInit {

  alquileres: Alquiler[] = [];
  cargando = true;
  errorMsg = '';

  constructor(private alquilerService: AlquilerService) {}

  ngOnInit(): void {
    this.obtenerAlquileres();
  }

  obtenerAlquileres(): void {
    this.alquilerService.listar().subscribe({
      next: (data) => {
        this.alquileres = data;
        this.cargando = false;
      },
      error: (err) => {
        this.errorMsg = 'Error al cargar los alquileres.';
        console.error(err);
        this.cargando = false;
      }
    });
  }

  finalizarAlquiler(alquiler: Alquiler): void {
  if (!alquiler.id_alquiler) return;

  // Solo por ejemplo, marcarlo como FINALIZADO
  alquiler.estado = 'FINALIZADO';

  this.alquilerService.actualizar(alquiler.id_alquiler, alquiler).subscribe({
    next: (res) => {
      console.log('Alquiler finalizado:', res);
      alert('✅ Alquiler finalizado correctamente');
      this.obtenerAlquileres(); 
    },
    error: (err) => {
      console.error('Error al finalizar alquiler:', err);
      alert('❌ Error al finalizar alquiler');
    }
  });
}

}
