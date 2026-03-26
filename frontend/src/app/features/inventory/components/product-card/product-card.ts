import { CurrencyPipe, NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';

import { ProductoInventario } from '../../../../core/models/producto-inventario.model';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CurrencyPipe, NgClass],
  templateUrl: './product-card.html',
  styleUrl: './product-card.scss'
})
export class ProductCard {
  @Input() product!: ProductoInventario;

  get isLowStock(): boolean {
    return this.product.stock <= this.product.stockMinimo;
  }

  get stockProgress(): number {
    if (!this.product.stockMaximo || this.product.stockMaximo <= 0) {
      return 0;
    }

    const progress = (this.product.stock / this.product.stockMaximo) * 100;
    return Math.min(progress, 100);
  }
}
