import { Component } from '@angular/core';

// ⚠️ IMPORTS CORRECTOS (verifica rutas)
import { SalesForm } from '../../components/sales-form/sales-form';
import { SalesSummary } from '../../components/sales-summary/sales-summary';
import { RecentActivityTable } from '../../components/recent-activity-table/recent-activity-table';

@Component({
  selector: 'app-sales-page',
  standalone: true,
  imports: [
    SalesForm,
    SalesSummary,
    RecentActivityTable
  ],
  templateUrl: './sales-page.html',
  styleUrl: './sales-page.scss'
})
export class SalesPage {}
