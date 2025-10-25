import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ruta } from '../models/ruta';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RutaService {


  private apiUrl = `${environment.apiUrl}/rutas`;

  constructor(private http: HttpClient) {}


  listar(): Observable<Ruta[]> {
    return this.http.get<Ruta[]>(this.apiUrl);
  }


  registrar(ruta: Ruta): Observable<Ruta> {
    return this.http.post<Ruta>(this.apiUrl, ruta);
  }


  actualizar(id: number, ruta: Ruta): Observable<Ruta> {
    return this.http.put<Ruta>(`${this.apiUrl}/${id}`, ruta);
  }
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
