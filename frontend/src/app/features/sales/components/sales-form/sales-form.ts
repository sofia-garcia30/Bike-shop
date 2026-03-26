import { Component, inject } from '@angular/core';
import { VentaService } from '../../../../core/services/venta';

@Component({
  selector: 'app-sales-form',
  standalone: true,
  templateUrl: './sales-form.html',
  styleUrl: './sales-form.scss'
})
export class SalesForm {

  private ventaService = inject(VentaService);

  cliente = '';
  bicicleta = '';
  cantidad = 1;
  precio = 0;

  guardarVenta() {
    const payload = {
      documentoCliente: this.cliente,
      codigoBicicleta: 1, // luego lo hacemos dinámico
      cantidad: this.cantidad,
      precioUnitario: this.precio
    };

    this.ventaService.crearVenta(payload).subscribe({
      next: () => {
        alert('Venta registrada correctamente');
      },
      error: (err) => {
        console.error(err);
        alert('Error al registrar venta');
      }
    });
  }
}
