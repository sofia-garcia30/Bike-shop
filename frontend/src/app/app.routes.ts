import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      {
        path: 'inventory',
        loadComponent: () =>
          import('./features/inventory/pages/inventory-page/inventory-page')
            .then(m => m.InventoryPage)
      },
      {
        path: 'sales',
        loadComponent: () =>
          import('./features/sales/pages/sales-page/sales-page')
            .then(m => m.SalesPage)
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/orders/pages/orders-page/orders-page')
            .then(m => m.OrdersPage)
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard-page/dashboard-page')
            .then(m => m.DashboardPage)
      },
      { path: '', redirectTo: 'inventory', pathMatch: 'full' }
    ]
  }
];
