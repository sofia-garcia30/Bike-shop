import { CommonModule, CurrencyPipe, NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, NgClass],
  templateUrl: './product-card.html',
  styleUrl: './product-card.scss'
})
export class ProductCard {
  @Input() product!: {
    brand: string;
    model: string;
    type: string;
    price: number;
    units: number;
  };
}
