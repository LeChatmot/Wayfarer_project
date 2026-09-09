import {inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthInitializerService {

  private authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);

  async initialize(): Promise<void> {
    try {
      const validation = await firstValueFrom(this.authService.validateToken());

      if (validation.valid) {
        return;
      }

      if (validation.refreshable) {
        await firstValueFrom(this.authService.refreshToken());
        return;
      }

      this.tokenStorage.clearTokens();
    } catch (error) {
      console.error(error);
      this.tokenStorage.clearTokens();
    }
  }
}
