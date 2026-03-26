import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/pedidos`;
  private proveedoresUrl = `${environment.apiUrl}/proveedores`;

  getPedidos(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getPedidoPorId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  crearPedido(payload: {
    idProveedor: number;
    detalles: {
      codigoBicicleta: number;
      cantidad: number;
      precioCostoUnitario: number;
    }[];
  }): Observable<any> {
    return this.http.post<any>(this.apiUrl, payload);
  }

  marcarRecibido(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/recibido`, {});
  }

  getProveedores(): Observable<any[]> {
    return this.http.get<any[]>(this.proveedoresUrl);
  }

  getBicicletas(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/bicicletas`);
  }
}
