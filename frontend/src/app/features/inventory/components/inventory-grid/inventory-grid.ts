import {
  Component,
  OnInit,
  OnDestroy,
  inject,
  ChangeDetectorRef
} from '@angular/core';
import { ProductCard } from '../product-card/product-card';

import { BicicletaService } from '../../../../core/services/bicicleta';
import { Bicicleta } from '../../../../core/models/bicicleta.model';
import { ProductoInventario } from '../../../../core/models/producto-inventario.model';

@Component({
  selector: 'app-inventory-grid',
  standalone: true,
  imports: [ProductCard],
  templateUrl: './inventory-grid.html',
  styleUrl: './inventory-grid.scss'
})
export class InventoryGrid implements OnInit, OnDestroy {
  private bicicletaService = inject(BicicletaService);
  private cdr = inject(ChangeDetectorRef);

  products: ProductoInventario[] = [];
  loading = true;
  error = '';

  private refreshHandler = () => this.cargarTodas();

  ngOnInit(): void {
    this.cargarTodas();
    window.addEventListener('inventory-refresh', this.refreshHandler);
  }

  ngOnDestroy(): void {
    window.removeEventListener('inventory-refresh', this.refreshHandler);
  }

  cargarTodas(): void {
    this.loading = true;
    this.error = '';

    this.bicicletaService.getAll().subscribe({
      next: (data: Bicicleta[]) => this.asignarProductos(data),
      error: (err: any) => this.manejarError(err)
    });
  }

  cargarPorMarca(marca: string): void {
    this.loading = true;
    this.error = '';

    this.bicicletaService.buscarPorMarca(marca).subscribe({
      next: (data: Bicicleta[]) => this.asignarProductos(data),
      error: (err: any) => this.manejarError(err)
    });
  }

  cargarPorTipo(tipo: string): void {
    this.loading = true;
    this.error = '';

    this.bicicletaService.buscarPorTipo(tipo).subscribe({
      next: (data: Bicicleta[]) => this.asignarProductos(data),
      error: (err: any) => this.manejarError(err)
    });
  }

  cargarStockBajo(): void {
    this.loading = true;
    this.error = '';

    this.bicicletaService.getStockBajo().subscribe({
      next: (data: Bicicleta[]) => this.asignarProductos(data),
      error: (err: any) => this.manejarError(err)
    });
  }

  private asignarProductos(data: Bicicleta[]): void {
    this.products = data.map((item) => ({
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

    this.loading = false;
    this.error = '';
    this.cdr.detectChanges();
  }

  private manejarError(err: any): void {
    console.error('Error al cargar inventario:', err);
    this.error = 'No se pudo cargar el inventario desde el backend.';
    this.loading = false;
    this.cdr.detectChanges();
  }
}
