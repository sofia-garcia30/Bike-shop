import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from '../../../../core/services/pedido.service';

@Component({
  selector: 'app-orders-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders-page.html',
  styleUrl: './orders-page.scss'
})
export class OrdersPage implements OnInit {
  private pedidoService = inject(PedidoService);

  // Listas
  pedidos: any[] = [];
  proveedores: any[] = [];
  bicicletas: any[] = [];

  // Estado
  loadingPedidos = true;
  errorPedidos = '';
  guardando = false;
  mensajeExito = '';
  errorForm = '';

  // Formulario
  idProveedorSeleccionado: number | null = null;
  detalles: { codigoBicicleta: number; cantidad: number; precioCostoUnitario: number }[] = [];

  // Ítem en edición
  codigoBicicletaItem: number | null = null;
  cantidadItem = 1;
  precioCostoItem = 0;

  ngOnInit(): void {
    this.cargarPedidos();
    this.cargarProveedores();
    this.cargarBicicletas();
  }

  cargarPedidos(): void {
    this.loadingPedidos = true;
    this.pedidoService.getPedidos().subscribe({
      next: (data) => {
        this.pedidos = data;
        this.loadingPedidos = false;
      },
      error: () => {
        this.errorPedidos = 'No se pudieron cargar los pedidos.';
        this.loadingPedidos = false;
      }
    });
  }

  cargarProveedores(): void {
    this.pedidoService.getProveedores().subscribe({
      next: (data) => { this.proveedores = data; },
      error: () => {}
    });
  }

  cargarBicicletas(): void {
    this.pedidoService.getBicicletas().subscribe({
      next: (data) => { this.bicicletas = data; },
      error: () => {}
    });
  }

  agregarDetalle(): void {
    if (!this.codigoBicicletaItem) {
      this.errorForm = 'Selecciona una bicicleta.';
      return;
    }
    if (this.cantidadItem <= 0) {
      this.errorForm = 'La cantidad debe ser mayor a 0.';
      return;
    }
    if (this.precioCostoItem <= 0) {
      this.errorForm = 'El precio de costo debe ser mayor a 0.';
      return;
    }
    this.detalles.push({
      codigoBicicleta: Number(this.codigoBicicletaItem),
      cantidad: Number(this.cantidadItem),
      precioCostoUnitario: Number(this.precioCostoItem)
    });
    this.codigoBicicletaItem = null;
    this.cantidadItem = 1;
    this.precioCostoItem = 0;
    this.errorForm = '';
  }

  eliminarDetalle(index: number): void {
    this.detalles.splice(index, 1);
  }

  obtenerNombreBicicleta(codigo: number): string {
    const b = this.bicicletas.find(b => Number(b.codigo) === Number(codigo));
    return b ? `${b.marca} ${b.modelo}` : `Código ${codigo}`;
  }

  guardarPedido(): void {
    this.mensajeExito = '';
    this.errorForm = '';

    if (!this.idProveedorSeleccionado) {
      this.errorForm = 'Selecciona un proveedor.';
      return;
    }
    if (this.detalles.length === 0) {
      this.errorForm = 'Agrega al menos una bicicleta al pedido.';
      return;
    }

    this.guardando = true;
    this.pedidoService.crearPedido({
      idProveedor: Number(this.idProveedorSeleccionado),
      detalles: this.detalles
    }).subscribe({
      next: () => {
        this.guardando = false;
        this.mensajeExito = 'Pedido registrado correctamente.';
        this.resetForm();
        this.cargarPedidos();
      },
      error: (err: any) => {
        this.guardando = false;
        this.errorForm = err?.error?.mensaje || 'No se pudo registrar el pedido.';
      }
    });
  }

  marcarRecibido(id: number): void {
    this.pedidoService.marcarRecibido(id).subscribe({
      next: () => { this.cargarPedidos(); },
      error: (err: any) => {
        alert(err?.error?.mensaje || 'Error al marcar pedido como recibido.');
      }
    });
  }

  resetForm(): void {
    this.idProveedorSeleccionado = null;
    this.detalles = [];
    this.codigoBicicletaItem = null;
    this.cantidadItem = 1;
    this.precioCostoItem = 0;
  }
}
