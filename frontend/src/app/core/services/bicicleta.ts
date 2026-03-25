import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Bicicleta } from '../models/bicicleta.model';

@Injectable({
  providedIn: 'root'
})
export class BicicletaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/bicicletas`;

  getAll(): Observable<Bicicleta[]> {
    return this.http.get<Bicicleta[]>(this.apiUrl);
  }

  getByCodigo(codigo: number): Observable<Bicicleta> {
    return this.http.get<Bicicleta>(`${this.apiUrl}/${codigo}`);
  }

  buscarPorMarca(marca: string): Observable<Bicicleta[]> {
    return this.http.get<Bicicleta[]>(`${this.apiUrl}/buscar?marca=${encodeURIComponent(marca)}`);
  }

  buscarPorTipo(tipo: string): Observable<Bicicleta[]> {
    return this.http.get<Bicicleta[]>(`${this.apiUrl}/buscar?tipo=${encodeURIComponent(tipo)}`);
  }

  getStockBajo(): Observable<Bicicleta[]> {
    return this.http.get<Bicicleta[]>(`${this.apiUrl}/stock-bajo`);
  }
}
