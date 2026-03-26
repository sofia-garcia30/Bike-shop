import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cliente } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private apiUrl = `${environment.apiUrl}/clientes`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  buscarPorDocumento(documento: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${documento}`);
  }

  buscarPorTermino(termino: string): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.apiUrl}/buscar?termino=${termino}`);
  }

  crear(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  actualizar(documento: string, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${documento}`, cliente);
  }
}