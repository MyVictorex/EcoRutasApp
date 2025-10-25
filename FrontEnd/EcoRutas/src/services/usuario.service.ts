import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Usuario } from '../models/usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private apiUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}


  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  insertar(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }


  actualizar(id_usuario: number, datos: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id_usuario}`, datos);
  }

 
  eliminar(id_usuario: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id_usuario}`);
  }
buscarPorId(id: number): Observable<Usuario> {
  return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
}

 

  login(correo: string, password: string): Observable<{ token: string, usuario: Usuario }> {
    return this.http.post<{ token: string, usuario: Usuario }>(
      `${this.apiUrl}/login`,
      { correo, password }
    );
  }
}
