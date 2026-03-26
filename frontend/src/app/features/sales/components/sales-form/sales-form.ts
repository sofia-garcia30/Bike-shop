import { Component, EventEmitter, Output, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BicicletaService } from '../../../../core/services/bicicleta';
import { VentaService } from '../../../../core/services/venta';

@Component({
  selector: 'app-sales-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './sales-form.html',
  styleUrl: './sales-form.scss'
})
export class SalesForm implements OnInit {
  @Output() ventaGuardada = new EventEmitter<void>();

  private bicicletaService = inject(BicicletaService);
  private ventaService = inject(VentaService);

  bicicletas: any[] = [];

  cliente = '';
  formaPago = 'efectivo';
  bicicletaSeleccionada: any = null;
  cantidad = 1;
  precio = 0;

  guardando = false;
  mensaje = '';
  error = '';

  ngOnInit(): void {
    this.bicicletaService.getAll().subscribe({
      next: (data: any[]) => {
        this.bicicletas = data;
      },
      error: (err: any) => {
        console.error('Error cargando bicicletas', err);
        this.error = 'No se pudieron cargar las bicicletas.';
      }
    });
  }

  seleccionarBicicleta(event: Event): void {
    const select = event.target as HTMLSelectElement;
    const codigo = Number(select.value);

    this.bicicletaSeleccionada =
      this.bicicletas.find((b) => Number(b.codigo) === codigo) || null;

    this.precio = this.bicicletaSeleccionada
      ? Number(this.bicicletaSeleccionada.precioVenta)
      : 0;
  }

  guardarVenta(event?: Event): void {
    event?.preventDefault();

    this.mensaje = '';
    this.error = '';

    if (!this.cliente.trim()) {
      this.error = 'Ingresa el documento del cliente.';
      return;
    }

    if (!this.bicicletaSeleccionada) {
      this.error = 'Selecciona una bicicleta.';
      return;
    }

    if (this.cantidad <= 0) {
      this.error = 'La cantidad debe ser mayor a 0.';
      return;
    }

    const payload = {
      documentoCliente: this.cliente.trim(),
      formaPago: this.formaPago,
      detalles: [
        {
          codigoBicicleta: Number(this.bicicletaSeleccionada.codigo),
          cantidad: Number(this.cantidad)
        }
      ]
    };

    this.guardando = true;

    this.ventaService.crearVenta(payload).subscribe({
      next: () => {
        this.guardando = false;
        this.mensaje = 'Venta registrada correctamente.';
        this.resetForm();
        this.ventaGuardada.emit();
      },
      error: (err: any) => {
        this.guardando = false;
        console.error('Error al registrar venta', err);
        this.error = err?.error?.mensaje || 'No se pudo registrar la venta.';
      }
    });
  }

  resetForm(): void {
    this.cliente = '';
    this.formaPago = 'efectivo';
    this.bicicletaSeleccionada = null;
    this.cantidad = 1;
    this.precio = 0;
  }
}
