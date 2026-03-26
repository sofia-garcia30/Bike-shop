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

  mostrarFiltros = false;

  marcaSeleccionada: string | null = null;
  tipoSeleccionado: string | null = null;
  soloStockBajo = false;

  toggleFiltros(): void {
    this.mostrarFiltros = !this.mostrarFiltros;
  }

  cerrarFiltros(): void {
    this.mostrarFiltros = false;
  }

  seleccionarMarca(marca: string | null): void {
    this.marcaSeleccionada = marca;
  }

  seleccionarTipo(tipo: string | null): void {
    this.tipoSeleccionado = tipo;
  }

  toggleStockBajo(): void {
    this.soloStockBajo = !this.soloStockBajo;
  }

  limpiarFiltros(): void {
    this.marcaSeleccionada = null;
    this.tipoSeleccionado = null;
    this.soloStockBajo = false;
    this.inventoryGrid.cargarTodas();
  }

  aplicarFiltros(): void {
    if (this.soloStockBajo) {
      this.inventoryGrid.cargarStockBajo();
      this.cerrarFiltros();
      return;
    }

    if (this.marcaSeleccionada) {
      this.inventoryGrid.cargarPorMarca(this.marcaSeleccionada);
      this.cerrarFiltros();
      return;
    }

    if (this.tipoSeleccionado) {
      this.inventoryGrid.cargarPorTipo(this.tipoSeleccionado);
      this.cerrarFiltros();
      return;
    }

    this.inventoryGrid.cargarTodas();
    this.cerrarFiltros();
  }
}
