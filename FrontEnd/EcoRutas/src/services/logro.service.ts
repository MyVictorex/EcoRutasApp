import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Logro } from '../models/logro';

@Injectable({
  providedIn: 'root'
})
export class LogroService {

  private apiUrl = `${environment.apiUrl}/logros`;

  constructor(private http: HttpClient) {}

 
  listar(): Observable<Logro[]> {
    return this.http.get<Logro[]>(this.apiUrl);
  }


  registrar(logro: Logro): Observable<Logro> {
    return this.http.post<Logro>(this.apiUrl, logro);
  }


  actualizar(id_logro: number, datos: Logro): Observable<Logro> {
    return this.http.put<Logro>(`${this.apiUrl}/${id_logro}`, datos);
  }
}
