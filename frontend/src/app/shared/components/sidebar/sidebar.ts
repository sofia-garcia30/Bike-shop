import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar {
  items = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Inventario', route: '/inventory' },
    { label: 'Ventas', route: '/sales' },
    { label: 'Pedidos', route: '/orders' }
  ];
}
