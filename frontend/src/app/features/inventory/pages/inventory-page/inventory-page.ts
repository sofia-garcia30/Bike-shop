import { Component } from '@angular/core';
import { InventoryGrid } from '../../components/inventory-grid/inventory-grid';

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [InventoryGrid],
  templateUrl: './inventory-page.html',
  styleUrl: './inventory-page.scss'
})
export class InventoryPage {
}
