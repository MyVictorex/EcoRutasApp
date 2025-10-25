import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { HistorialRuta } from '../models/historial-ruta';

@Injectable({
  providedIn: 'root'
})
export class HistorialRutaService {

  private apiUrl = `${environment.apiUrl}/Historial`;

  constructor(private http: HttpClient) {}


  listar(): Observable<HistorialRuta[]> {
    return this.http.get<HistorialRuta[]>(this.apiUrl);
  }


  registrar(historial: HistorialRuta): Observable<HistorialRuta> {
    return this.http.post<HistorialRuta>(this.apiUrl, historial);
  }
}
