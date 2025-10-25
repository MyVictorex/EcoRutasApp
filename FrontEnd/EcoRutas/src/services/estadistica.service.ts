import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Estadistica } from '../models/estadistica';

@Injectable({
  providedIn: 'root'
})
export class EstadisticaService {

  private apiUrl = `${environment.apiUrl}/estadisticas`;

  constructor(private http: HttpClient) {}


  listar(): Observable<Estadistica[]> {
    return this.http.get<Estadistica[]>(this.apiUrl);
  }


  generar(): Observable<Estadistica> {
    return this.http.post<Estadistica>(this.apiUrl, {}); 
  }
}
