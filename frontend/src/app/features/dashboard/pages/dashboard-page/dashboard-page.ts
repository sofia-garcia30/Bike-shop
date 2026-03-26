import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';          // ✅ CurrencyPipe eliminado
import { Router } from '@angular/router';
import { DashboardService } from '../../../../core/services/dashboard.service'; // ✅ .service añadido

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule],                               // ✅ CurrencyPipe eliminado
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss'
})
export class DashboardPage implements OnInit {
  private dashboardService = inject(DashboardService);
  private router = inject(Router);

  loading = true;
  error = '';

  resumen: any = null;
  topBicicletas: any[] = [];

  alerts: string[] = [];
  activity: any[] = [];

  ngOnInit(): void {
    this.cargarDashboard();
  }

  cargarDashboard(): void {
    this.loading = true;
    this.error = '';

    this.dashboardService.getResumen().subscribe({
      next: (data: any) => {                             // ✅ tipo añadido
        this.resumen = data;
        this.loading = false;
        this.alerts = [];
        if (data.sinStock > 0) {
          this.alerts.push(`${data.sinStock} bicicleta(s) sin stock`);
        }
        if (data.stockBajo > 0) {
          this.alerts.push(`${data.stockBajo} bicicleta(s) con stock bajo`);
        }
        if (this.alerts.length === 0) {
          this.alerts.push('Stock en niveles normales');
        }
      },
      error: (err: any) => {                             // ✅ tipo añadido
        console.error('Error cargando dashboard', err);
        this.error = 'No se pudo cargar el resumen.';
        this.loading = false;
      }
    });

    this.dashboardService.getTopBicicletas().subscribe({
      next: (data: any[]) => {                           // ✅ tipo añadido
        this.topBicicletas = data.slice(0, 5);
      },
      error: (err: any) => {                             // ✅ tipo añadido
        console.error('Error cargando top bicicletas', err);
      }
    });
  }

  irAVentas(): void {
    this.router.navigate(['/sales']);
  }

  irAPedidos(): void {
    this.router.navigate(['/orders']);
  }

  exportar(): void {
    const data = {
      resumen: this.resumen,
      topBicicletas: this.topBicicletas,
      fecha: new Date().toLocaleString('es-CO')
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reporte-dashboard-${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}