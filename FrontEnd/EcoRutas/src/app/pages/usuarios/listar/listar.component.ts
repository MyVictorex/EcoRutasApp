import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UsuarioService } from '../../../../services/usuario.service';
import { Usuario } from '../../../../models/usuario';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-listar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './listar.component.html',
  styleUrl: './listar.component.scss'
})
export class ListarComponent implements OnInit {
  usuarios: Usuario[] = [];
  cargando = false;
  errorMsg = '';
  mensaje = '';
  idBuscar: number | null = null;
  usuarioEncontrado: Usuario | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.listarUsuarios();
  }

  listarUsuarios(): void {
    this.cargando = true;
    this.usuarioService.listar().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.cargando = false;
      },
      error: () => {
        this.errorMsg = 'Error al cargar los usuarios.';
        this.cargando = false;
      }
    });
  }

  buscarPorId(): void {
    if (!this.idBuscar) {
      this.mensaje = 'Ingrese un ID válido.';
      return;
    }
    this.cargando = true;
    this.usuarioService.buscarPorId(this.idBuscar).subscribe({
      next: (data) => {
        this.usuarioEncontrado = data;
        this.cargando = false;
        this.mensaje = '';
      },
      error: () => {
        this.usuarioEncontrado = null;
        this.mensaje = 'Usuario no encontrado.';
        this.cargando = false;
      }
    });
  }

  eliminarUsuario(id_usuario?: number): void {
    if (!id_usuario) return;
    if (!confirm('¿Seguro que deseas eliminar este usuario?')) return;

    this.usuarioService.eliminar(id_usuario).subscribe({
      next: () => {
        this.mensaje = 'Usuario eliminado correctamente ✅';
        this.listarUsuarios();
      },
      error: () => {
        this.mensaje = 'Error al eliminar el usuario ❌';
      }
    });
  }

  editarUsuario(id_usuario?: number): void {
    if (!id_usuario) return;
    this.router.navigate([`/usuarios/editar/${id_usuario}`]);
  }
}
