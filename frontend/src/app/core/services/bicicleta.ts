import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BicicletaService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/bicicletas`;

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  buscarPorMarca(marca: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/buscar?marca=${marca}`);
  }

  buscarPorTipo(tipo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/buscar?tipo=${tipo}`);
  }

  getStockBajo(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stock-bajo`);
  }

  crear(bicicleta: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, bicicleta);
  }

  actualizar(codigo: number, bicicleta: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${codigo}`, bicicleta);
  }

  eliminar(codigo: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
