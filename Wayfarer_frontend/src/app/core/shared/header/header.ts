import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, MatButton, MatFabButton, MatIcon],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
  onLogout(): void {
    this.authService.logout().subscribe(value => {
      console.log("Logging out")
    });
    this.router.navigate(['/map']);
  }
}
