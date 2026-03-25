import { Component } from '@angular/core';
import { ProductCard } from '../product-card/product-card';

@Component({
  selector: 'app-inventory-grid',
  standalone: true,
  imports: [ProductCard],
  templateUrl: './inventory-grid.html',
  styleUrl: './inventory-grid.scss'
})
export class InventoryGrid {
  products = [
    {
      brand: 'Specialized',
      model: 'S-Works Tarmac SL8',
      type: 'Ruta',
      price: 12500,
      units: 4
    },
    {
      brand: 'Trek',
      model: 'Fuel EX Gen 6',
      type: 'MTB',
      price: 5800,
      units: 12
    },
    {
      brand: 'Cannondale',
      model: 'Bad Boy 1',
      type: 'Urbana',
      price: 2100,
      units: 2
    },
    {
      brand: 'Canyon',
      model: 'Grizl CF SL 8',
      type: 'Gravel',
      price: 3200,
      units: 7
    }
  ];
}
