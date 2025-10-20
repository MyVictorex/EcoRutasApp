import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterModule } from '@angular/router';

import { Alquiler } from '../../../../models/alquiler';
import { Usuario } from '../../../../models/usuario';
import { Ruta } from '../../../../models/ruta';
import { Vehiculo } from '../../../../models/vehiculo';

import { AlquilerService } from '../../../../services/alquiler.service';
import { UsuarioService } from '../../../../services/usuario.service';
import { RutaService } from '../../../../services/ruta.service';
import { VehiculoService } from '../../../../services/vehiculo.service';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, FormsModule,RouterModule],
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {
  alquiler: Alquiler = {
    id_alquiler: 0,
    usuario: { id_usuario: 0 } as Usuario,
    vehiculo: { id_vehiculo: 0 } as Vehiculo,
    ruta: { id_ruta: 0, usuario: { id_usuario: 0 } as Usuario } as Ruta,
    fecha_inicio: new Date().toISOString(),
    fecha_fin: new Date().toISOString(),
    costo: 0,
    estado: 'EN_CURSO'
  };

  usuarios: Usuario[] = [];
  rutas: Ruta[] = [];
  vehiculos: Vehiculo[] = [];

  cargando = false;
  mensaje = '';
  editando = false;

  constructor(
    private alquilerService: AlquilerService,
    private usuarioService: UsuarioService,
    private rutaService: RutaService,
    private vehiculoService: VehiculoService,
    private route: ActivatedRoute,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDatos();

    // 🔍 Verificar si hay un ID en la URL (modo edición)
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.alquilerService.listar().subscribe((data) => {
        const encontrado = data.find(a => a.id_alquiler === +id);
        if (encontrado) this.alquiler = encontrado;
      });
    }
  }

  cargarDatos(): void {
    this.usuarioService.listar().subscribe(data => (this.usuarios = data));
    this.rutaService.listar().subscribe(data => (this.rutas = data));
    this.vehiculoService.listar().subscribe(data => (this.vehiculos = data));
  }

  guardar(): void {
  this.cargando = true;

  // 💡 Si no está editando, eliminamos el id para que el backend lo autogenere
  if (!this.editando) {
    delete this.alquiler.id_alquiler;
  }

  const request = this.editando
    ? this.alquilerService.actualizar(this.alquiler.id_alquiler!, this.alquiler)
    : this.alquilerService.registrar(this.alquiler);

  request.subscribe({
    next: () => {
      this.mensaje = this.editando
        ? '✅ Alquiler actualizado correctamente'
        : '✅ Alquiler registrado correctamente';
      this.cargando = false;

      setTimeout(() => this.router.navigate(['/alquileres']), 1500);
    },
    error: (err) => {
      console.error(err);
      this.mensaje = '❌ Error al guardar el alquiler';
      this.cargando = false;
    }
  });
}

}
