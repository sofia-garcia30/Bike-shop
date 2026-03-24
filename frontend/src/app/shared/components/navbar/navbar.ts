import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  navItems = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Inventory', route: '/inventory' },
    { label: 'Sales', route: '/sales' },
    { label: 'Orders', route: '/orders' }
  ];
}
