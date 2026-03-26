import { Routes } from '@angular/router';
import { SalesPage } from './features/sales/pages/sales-page/sales-page';

export const routes: Routes = [
  { path: 'inventory', loadComponent: () => import('./features/inventory/pages/inventory-page/inventory-page').then(m => m.InventoryPage) },

  // ✅ AGREGA ESTO
  { path: 'sales', component: SalesPage },

  { path: '', redirectTo: 'inventory', pathMatch: 'full' }
];
