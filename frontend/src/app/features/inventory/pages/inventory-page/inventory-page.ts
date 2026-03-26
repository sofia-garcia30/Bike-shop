import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventoryGrid } from '../../components/inventory-grid/inventory-grid';
import { BicicletaService } from '../../../../core/services/bicicleta';
import { inject } from '@angular/core';

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [InventoryGrid, FormsModule],
  templateUrl: './inventory-page.html',
  styleUrl: './inventory-page.scss'
})
export class InventoryPage {
  @ViewChild(InventoryGrid) inventoryGrid!: InventoryGrid;
  private bicicletaService = inject(BicicletaService);

  mostrarFiltros = false;
  mostrarFormulario = false;
  bicicletaEditando: any = null;
  marcaSeleccionada: string | null = null;
  tipoSeleccionado: string | null = null;
  soloStockBajo = false;

  form = {
    marca: '',
    modelo: '',
    tipo: 'Montaña',
    precioCosto: 0,
    precioVenta: 0,
    descripcion: ''
  };

  abrirFormulario(bicicleta?: any): void {
    this.bicicletaEditando = bicicleta || null;
    if (bicicleta) {
      this.form = {
        marca: bicicleta.brand,
        modelo: bicicleta.model,
        tipo: bicicleta.type,
        precioCosto: bicicleta.precioCosto || 0,
        precioVenta: bicicleta.price,
        descripcion: bicicleta.description
      };
    } else {
      this.form = {
        marca: '', modelo: '', tipo: 'Montaña',
        precioCosto: 0, precioVenta: 0, descripcion: ''
      };
    }
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    this.mostrarFormulario = false;
    this.bicicletaEditando = null;
  }

  guardarBicicleta(): void {
    if (this.bicicletaEditando) {
      this.bicicletaService.actualizar(this.bicicletaEditando.codigo, this.form)
        .subscribe({
          next: () => {
            this.cerrarFormulario();
            this.inventoryGrid.cargarTodas();
          },
          error: (err: any) => console.error(err)
        });
    } else {
      this.bicicletaService.crear(this.form).subscribe({
        next: () => {
          this.cerrarFormulario();
          this.inventoryGrid.cargarTodas();
        },
        error: (err: any) => console.error(err)
      });
    }
  }

  toggleFiltros(): void { this.mostrarFiltros = !this.mostrarFiltros; }
  cerrarFiltros(): void { this.mostrarFiltros = false; }
  seleccionarMarca(marca: string | null): void { this.marcaSeleccionada = marca; }
  seleccionarTipo(tipo: string | null): void { this.tipoSeleccionado = tipo; }
  toggleStockBajo(): void { this.soloStockBajo = !this.soloStockBajo; }

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
