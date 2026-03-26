import { Component } from '@angular/core';

@Component({
  selector: 'app-recent-activity-table',
  standalone: true,
  imports: [],
  templateUrl: './recent-activity-table.html',
  styleUrl: './recent-activity-table.scss'
})
export class RecentActivityTable {
  activities = [
    {
      cliente: 'Laura Gómez',
      producto: 'GW MTB 29',
      fecha: '25/03/2026',
      total: '$ 1.800.000',
      estado: 'Completada'
    },
    {
      cliente: 'Carlos Ruiz',
      producto: 'Bicicleta urbana',
      fecha: '25/03/2026',
      total: '$ 950.000',
      estado: 'Pendiente'
    },
    {
      cliente: 'Andrea Torres',
      producto: 'Ruta Pro',
      fecha: '24/03/2026',
      total: '$ 2.400.000',
      estado: 'Completada'
    }
  ];
}
