import { Component } from '@angular/core';

@Component({
  selector: 'app-supplier-card',
  standalone: true,
  imports: [],
  templateUrl: './supplier-card.html',
  styleUrl: './supplier-card.scss'
})
export class SupplierCard {
  suppliers = [
    { name: 'GW Distribuciones', status: 'Activo', lead: '48 horas' },
    { name: 'Bike Parts Co.', status: 'Preferente', lead: '72 horas' },
    { name: 'Urban Wheels SAS', status: 'Activo', lead: '24 horas' }
  ];
}
