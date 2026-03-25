import { Component, ViewChild } from '@angular/core';
import { InventoryGrid } from '../../components/inventory-grid/inventory-grid';

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [InventoryGrid],
  templateUrl: './inventory-page.html',
  styleUrl: './inventory-page.scss'
})
export class InventoryPage {
  @ViewChild(InventoryGrid) inventoryGrid!: InventoryGrid;

  filtrarTodas(): void {
    this.inventoryGrid.cargarTodas();
  }

  filtrarMarcaGW(): void {
    this.inventoryGrid.cargarPorMarca('GW');
  }

  filtrarTipoMontana(): void {
    this.inventoryGrid.cargarPorTipo('Montaña');
  }

  filtrarStockBajo(): void {
    this.inventoryGrid.cargarStockBajo();
  }
}
