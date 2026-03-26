import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/dashboard`;

  getResumen(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  getTopBicicletas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/top-bicicletas`);
  }
}