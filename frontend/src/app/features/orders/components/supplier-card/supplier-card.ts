import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from '../../../../core/services/pedido';
import { ProveedorService } from '../../../../core/services/proveedor';
import { BicicletaService } from '../../../../core/services/bicicleta';

@Component({
  selector: 'app-supplier-card',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier-card.html',
  styleUrl: './supplier-card.scss'
})
export class SupplierCard implements OnInit {
  private pedidoService = inject(PedidoService);
  private proveedorService = inject(ProveedorService);
  private bicicletaService = inject(BicicletaService);

  pedidos: any[] = [];
  proveedores: any[] = [];
  bicicletas: any[] = [];
  loading = true;

  mostrarFormularioProveedor = false;
  mostrarFormularioPedido = false;

  nuevoProveedor = {
    nombre: '',
    telefono: '',
    frecuenciaEntrega: '48 horas'
  };

  nuevoPedido = {
    proveedorId: null,
    detalles: [{ bicicletaId: null, cantidad: 1, precioCosto: 0 }]
  };

  ngOnInit(): void {
    this.cargarDatos();
    this.cargarBicicletas();
  }

  cargarDatos(): void {
    this.loading = true;
    this.pedidoService.getAll().subscribe({
      next: (data) => {
        this.pedidos = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });

    this.proveedorService.getAll().subscribe({
      next: (data) => this.proveedores = data
    });
  }

  cargarBicicletas(): void {
    this.bicicletaService.getAll().subscribe({
      next: (data) => this.bicicletas = data
    });
  }

  marcarRecibido(id: number): void {
    this.pedidoService.marcarRecibido(id).subscribe({
      next: () => this.cargarDatos()
    });
  }

  abrirFormularioProveedor(): void {
    this.mostrarFormularioProveedor = true;
  }

  cerrarFormularioProveedor(): void {
    this.mostrarFormularioProveedor = false;
    this.nuevoProveedor = { nombre: '', telefono: '', frecuenciaEntrega: '48 horas' };
  }

  guardarProveedor(): void {
    this.proveedorService.crear(this.nuevoProveedor).subscribe({
      next: () => {
        this.cerrarFormularioProveedor();
        this.cargarDatos();
      }
    });
  }

  abrirFormularioPedido(): void {
    this.mostrarFormularioPedido = true;
  }

  cerrarFormularioPedido(): void {
    this.mostrarFormularioPedido = false;
    this.nuevoPedido = {
      proveedorId: null,
      detalles: [{ bicicletaId: null, cantidad: 1, precioCosto: 0 }]
    };
  }

  agregarDetalle(): void {
    this.nuevoPedido.detalles.push({ bicicletaId: null, cantidad: 1, precioCosto: 0 });
  }

  removerDetalle(index: number): void {
    this.nuevoPedido.detalles.splice(index, 1);
  }

guardarPedido(): void {
  // Validar que haya seleccionado un proveedor
  if (!this.nuevoPedido.proveedorId) {
    console.error('Debe seleccionar un proveedor');
    return;
  }

  // Validar que los detalles tengan datos
  const detallesValidos = this.nuevoPedido.detalles.filter((d: any) =>
    d.bicicletaId && d.cantidad > 0 && d.precioCosto > 0
  );

  if (detallesValidos.length === 0) {
    console.error('Debe agregar al menos un detalle válido');
    return;
  }

  const pedidoRequest = {
    idProveedor: Number(this.nuevoPedido.proveedorId),  // Asegura que es número
    detalles: detallesValidos.map((d: any) => ({
      codigoBicicleta: Number(d.bicicletaId),      // Asegura que es número
      cantidad: Number(d.cantidad),                 // Asegura que es número
      precioCostoUnitario: Number(d.precioCosto)    // Asegura que es número
    }))
  };

  console.log('Enviando pedido:', pedidoRequest); // Para debug

  this.pedidoService.crear(pedidoRequest).subscribe({
    next: (response: any) => {
      console.log('Pedido creado:', response);
      this.cerrarFormularioPedido();
      this.cargarDatos();
      // Mostrar mensaje de éxito

    },
    error: (err: any) => {
      console.error('Error al crear pedido:', err);
      // Mostrar mensaje de error

    }
  });
}
}
