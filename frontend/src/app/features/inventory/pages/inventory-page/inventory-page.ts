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

  marcaSeleccionada: string | null = null;
  tipoSeleccionado: string | null = null;
  soloStockBajo = false;

  seleccionarMarca(marca: string | null) {
    this.marcaSeleccionada = marca;
  }

  seleccionarTipo(tipo: string | null) {
    this.tipoSeleccionado = tipo;
  }

  toggleStockBajo() {
    this.soloStockBajo = !this.soloStockBajo;
  }

  aplicarFiltros() {
    if (this.soloStockBajo) {
      this.inventoryGrid.cargarStockBajo();
      return;
    }

    if (this.marcaSeleccionada) {
      this.inventoryGrid.cargarPorMarca(this.marcaSeleccionada);
      return;
    }

    if (this.tipoSeleccionado) {
      this.inventoryGrid.cargarPorTipo(this.tipoSeleccionado);
      return;
    }

    this.inventoryGrid.cargarTodas();
  }
}
