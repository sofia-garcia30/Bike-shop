import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss'
})
export class DashboardPage {
  metrics = [
    { label: 'Ventas del día', value: '$ 4.200.000', note: '+12% vs ayer' },
    { label: 'Stock total', value: '148', note: 'Unidades registradas' },
    { label: 'Alertas activas', value: '03', note: 'Stock crítico' }
  ];

  alerts = [
    'GW MTB 29 con stock bajo',
    'Pedido pendiente con Bike Parts Co.',
    'Venta reciente pendiente de confirmar'
  ];

  activity = [
    { title: 'Venta registrada', detail: 'GW MTB 29 - Laura Gómez', date: 'Hoy, 10:20 AM' },
    { title: 'Pedido creado', detail: 'Urban Wheels SAS - 8 unidades', date: 'Hoy, 09:10 AM' },
    { title: 'Inventario actualizado', detail: 'Ruta Pro - stock ajustado', date: 'Ayer, 05:40 PM' }
  ];
}
