import { Component } from '@angular/core';

@Component({
  selector: 'app-sales-summary',
  standalone: true,
  imports: [],
  templateUrl: './sales-summary.html',
  styleUrl: './sales-summary.scss'
})
export class SalesSummary {
  metrics = [
    { label: 'Ventas del día', value: '$ 4.200.000', note: '+12% vs ayer' },
    { label: 'Transacciones', value: '18', note: 'Operaciones registradas' },
    { label: 'Ticket promedio', value: '$ 233.000', note: 'Promedio por venta' }
  ];
}
