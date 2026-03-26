import { Component } from '@angular/core';

@Component({
  selector: 'app-orders-table',
  standalone: true,
  imports: [],
  templateUrl: './orders-table.html',
  styleUrl: './orders-table.scss'
})
export class OrdersTable {
  orders = [
    {
      proveedor: 'GW Distribuciones',
      producto: 'GW MTB 29',
      cantidad: 12,
      fecha: '28/03/2026',
      estado: 'En tránsito'
    },
    {
      proveedor: 'Bike Parts Co.',
      producto: 'Ruta Pro',
      cantidad: 5,
      fecha: '30/03/2026',
      estado: 'Pendiente'
    },
    {
      proveedor: 'Urban Wheels SAS',
      producto: 'Bicicleta urbana',
      cantidad: 8,
      fecha: '26/03/2026',
      estado: 'Recibido'
    }
  ];
}
