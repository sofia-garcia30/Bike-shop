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
}
