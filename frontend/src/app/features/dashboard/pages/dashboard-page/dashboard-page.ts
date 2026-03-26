import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss'
})
export class DashboardPage {

  constructor(private router: Router) {}

  metrics = [
    { label: 'Ventas del día', value: '$ 4.200.000', note: '+12% vs ayer' },
    { label: 'Stock total', value: '148', note: 'Unidades registradas' },
    { label: 'Alertas activas', value: '03', note: 'Stock crítico' }
  ];

  alerts = [
    'GW MTB 29 con stock bajo',
    'Pedido pendiente con proveedor',
    'Venta pendiente de confirmar'
  ];

  activity = [
    { title: 'Venta registrada', detail: 'GW MTB 29 - Laura Gómez', date: 'Hoy, 10:20 AM' },
    { title: 'Pedido creado', detail: 'Urban Wheels - 8 unidades', date: 'Hoy, 09:10 AM' },
    { title: 'Inventario actualizado', detail: 'Ruta Pro ajustado', date: 'Ayer, 05:40 PM' }
  ];

  // 🔥 BOTONES FUNCIONALES
  irAVentas() {
    this.router.navigate(['/sales']);
  }

  irAPedidos() {
    this.router.navigate(['/orders']);
  }

  exportar() {
    const data = [
      { producto: 'GW MTB 29', stock: 10, precio: 1800000 }
    ];

    const blob = new Blob([JSON.stringify(data)], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = 'reporte.json';
    a.click();

    window.URL.revokeObjectURL(url);
  }
}
