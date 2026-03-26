import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VentaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/ventas`;

  crearVenta(payload: {
    documentoCliente: string;
    formaPago: string;
    detalles: {
      codigoBicicleta: number;
      cantidad: number;
    }[];
  }): Observable<any> {
    return this.http.post(this.apiUrl, payload);
  }

  getVentas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
