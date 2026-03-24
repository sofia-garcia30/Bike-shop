import { Component } from '@angular/core';
import { InventoryFilters } from '../../components/inventory-filters/inventory-filters';
import { InventoryGrid } from '../../components/inventory-grid/inventory-grid';

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [InventoryFilters, InventoryGrid],
  templateUrl: './inventory-page.html',
  styleUrl: './inventory-page.scss'
})
export class InventoryPage {
}
