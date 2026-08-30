import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, MatButton],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  private readonly authService: AuthService  = inject(AuthService);
  private readonly router: Router = inject(Router);

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
