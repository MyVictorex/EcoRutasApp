import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Ruta } from '../../../../models/ruta';
import { RutaService } from '../../../../services/ruta.service';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {

  ruta: Ruta = this.nuevaRuta();

  cargando = false;
  mensaje = '';
  editando = false;

  constructor(
    private rutaService: RutaService,
    private route: ActivatedRoute,
    public router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.cargarRuta(+id);
    }
  }

  // 🔁 Cargar una ruta existente para editar
  cargarRuta(id: number): void {
    this.cargando = true;
    this.rutaService.listar().subscribe({
      next: (data) => {
        const encontrada = data.find(r => r.id_ruta === id);
        if (encontrada) this.ruta = encontrada;
        this.cargando = false;
      },
      error: (err) => {
        console.error(err);
        this.mensaje = 'Error al cargar la ruta ❌';
        this.cargando = false;
      }
    });
  }

  // 💾 Guardar (insertar o actualizar)
  guardar(): void {
    this.cargando = true;
    this.mensaje = '';

    // 🔥 Si no tiene id_ruta o es 0 → se convierte a null para insertar
    if (!this.ruta.id_ruta || this.ruta.id_ruta === 0) {
      this.ruta.id_ruta = null as any;
    }

    const request = this.editando
      ? this.rutaService.actualizar(this.ruta.id_ruta!, this.ruta)
      : this.rutaService.registrar(this.ruta);

    request.subscribe({
      next: () => {
        this.mensaje = this.editando
          ? 'Ruta actualizada correctamente ✅'
          : 'Ruta registrada correctamente ✅';

        this.cargando = false;
        setTimeout(() => this.router.navigate(['/rutas']), 1500);
      },
      error: (err) => {
        console.error(err);
        this.mensaje = 'Error al guardar la ruta ❌';
        this.cargando = false;
      }
    });
  }

  // 🧹 Resetear formulario
  limpiar(): void {
    this.ruta = this.nuevaRuta();
  }

  // 🔧 Crea un nuevo objeto ruta base
  private nuevaRuta(): Ruta {
    return {
      id_ruta: null as any, // ✅ Importante: null, no 0
      nombre: '',
      descripcion: '',
      punto_inicio: '',
      punto_fin: '',
      distancia_km: 0,
      tipo: 'BICICLETA',
      estado: 'ACTIVA',
      fecha_creacion: new Date().toISOString(),
      usuario: {
        id_usuario: 1, // 🔥 cambia esto por el usuario logueado después
        nombre: '',
        apellido: '',
        correo: '',
        password: '',
        rol: 'usuario',
        fecha_registro: ''
      }
    };
  }
}
