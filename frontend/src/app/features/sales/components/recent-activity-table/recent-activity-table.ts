import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VentaService } from '../../../../core/services/venta';

@Component({
  selector: 'app-recent-activity-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recent-activity-table.html',
  styleUrl: './recent-activity-table.scss'
})
export class RecentActivityTable implements OnInit {
  private ventaService = inject(VentaService);

  ventas: any[] = [];
  loading = true;
  error = '';

  ngOnInit(): void {
    this.cargarVentas();
    window.addEventListener('ventas-refresh', this.handleRefresh);
  }

  ngOnDestroy(): void {
    window.removeEventListener('ventas-refresh', this.handleRefresh);
  }

  private handleRefresh = () => {
    this.cargarVentas();
  };

  cargarVentas(): void {
    this.loading = true;
    this.error = '';

    this.ventaService.getVentas().subscribe({
      next: (data: any[]) => {
        this.ventas = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error cargando ventas', err);
        this.error = 'No se pudo cargar la actividad reciente.';
        this.loading = false;
      }
    });
  }

  obtenerProducto(venta: any): string {
    return venta?.detalles?.[0]?.marcaModelo || 'Sin detalle';
  }

  obtenerFecha(venta: any): string {
    if (!venta?.fecha) {
      return '';
    }

    return new Date(venta.fecha).toLocaleString('es-CO');
  }
}
