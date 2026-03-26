import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/pedidos`;
  private proveedorUrl = `${environment.apiUrl}/proveedores`;

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getProveedores(): Observable<any[]> {
    return this.http.get<any[]>(this.proveedorUrl);
  }

    crear(pedido: any): Observable<any> {
      console.log('Enviando al backend:', pedido); // Debug
      return this.http.post<any>(this.apiUrl, pedido);
    }

  marcarRecibido(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/recibido`, {});
  }
}
