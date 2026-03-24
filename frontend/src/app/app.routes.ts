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
      { path: '', redirectTo: 'inventory', pathMatch: 'full' }
    ]
  }
];
