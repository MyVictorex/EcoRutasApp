import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Alquiler } from '../models/alquiler';

@Injectable({
  providedIn: 'root'
})
export class AlquilerService {

  private apiUrl = `${environment.apiUrl}/alquileres`;

  constructor(private http: HttpClient) {}


  listar(): Observable<Alquiler[]> {
    return this.http.get<Alquiler[]>(this.apiUrl);
  }


  registrar(alquiler: Alquiler): Observable<Alquiler> {
    return this.http.post<Alquiler>(this.apiUrl, alquiler);
  }


  finalizar(id_alquiler: number, datos: Alquiler): Observable<Alquiler> {
    return this.http.put<Alquiler>(`${this.apiUrl}/${id_alquiler}/finalizar`, datos);
  }
}
