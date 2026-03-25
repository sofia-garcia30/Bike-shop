import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { ProductCard } from '../product-card/product-card';

import { BicicletaService } from '../../../../core/services/bicicleta';
import { Bicicleta } from '../../../../core/models/bicicleta.model';
import { ProductoInventario } from '../../../../core/models/producto-inventario.model';

@Component({
  selector: 'app-inventory-grid',
  standalone: true,
  imports: [ProductCard, CurrencyPipe],
  templateUrl: './inventory-grid.html',
  styleUrl: './inventory-grid.scss'
})
export class InventoryGrid implements OnInit {
  private bicicletaService = inject(BicicletaService);
  private cdr = inject(ChangeDetectorRef);

  products: ProductoInventario[] = [];
  loading = true;
  error = '';

  ngOnInit(): void {
    console.log('ENTRO A INVENTORY GRID');

    this.bicicletaService.getAll().subscribe({
      next: (data: Bicicleta[]) => {
        console.log('Bicicletas backend:', data);

        const mappedProducts: ProductoInventario[] = data.map((item) => ({
          codigo: item.codigo,
          brand: item.marca,
          model: item.modelo,
          type: item.tipo || 'Sin categoría',
          price: Number(item.precioVenta),
          stock: Number(item.cantidad),
          stockMinimo: Number(item.stockMinimo),
          stockMaximo: Number(item.stockMaximo),
          description: item.descripcion || '',
          image: ''
        }));

        this.products = [...mappedProducts];
        this.loading = false;
        this.error = '';

        console.log('Productos mapeados:', this.products);
        console.log('Total products length:', this.products.length);

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar inventario:', err);
        this.error = `No se pudo cargar el inventario. Estado: ${err.status || 'sin estado'}`;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
