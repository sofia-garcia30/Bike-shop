import { Component } from '@angular/core';
import { SupplierCard } from '../../components/supplier-card/supplier-card';

@Component({
  selector: 'app-orders-page',
  standalone: true,
  imports: [SupplierCard],
  templateUrl: './orders-page.html',
  styleUrl: './orders-page.scss'
})
export class OrdersPage {}
