import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UsuarioService } from '../../../../services/usuario.service';
import { Usuario } from '../../../../models/usuario';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {

  usuario: Usuario = {
    id_usuario: 0,
    nombre: '',
    apellido: '',
    correo: '',
    password: '',
    rol: 'USUARIO',
    fecha_registro: new Date().toISOString()
  };

  editando = false;
  cargando = false;
  mensaje = '';

  constructor(
    private usuarioService: UsuarioService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.cargarUsuario(Number(id));
    }
  }

  cargarUsuario(id: number): void {
    this.cargando = true;
    this.usuarioService.buscarPorId(id).subscribe({
      next: (data) => {
        this.usuario = data;
        this.cargando = false;
      },
      error: () => {
        this.mensaje = 'Error al cargar el usuario ❌';
        this.cargando = false;
      }
    });
  }

  guardar(): void {
  this.cargando = true;

 
  if (!this.editando) {
    delete this.usuario.id_usuario;
  }

  const request = this.editando
    ? this.usuarioService.actualizar(this.usuario.id_usuario!, this.usuario)
    : this.usuarioService.insertar(this.usuario);

  request.subscribe({
    next: () => {
      this.mensaje = this.editando
        ? '✅ Usuario actualizado correctamente'
        : '✅ Usuario registrado correctamente';
      this.cargando = false;
      setTimeout(() => this.router.navigate(['/usuarios']), 1200);
    },
    error: (err) => {
      console.error(err);
      this.mensaje = '❌ Error al guardar el usuario';
      this.cargando = false;
    }
  });
}


  volver(): void {
    setTimeout(() => {
      this.router.navigate(['/usuarios']);
    }, 1200);
  }
}
