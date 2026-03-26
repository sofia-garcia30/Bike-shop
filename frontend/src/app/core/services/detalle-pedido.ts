import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DetallePedidoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/detalles-pedido`;

  crear(detalle: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, detalle);
  }
}
