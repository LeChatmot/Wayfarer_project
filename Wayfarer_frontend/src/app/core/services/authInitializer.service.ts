import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthInitializerService {

  constructor(
    private tokenStorage: TokenStorageService,
    private authService: AuthService
  ) {}

  async initialize(): Promise<void> {
    if (!this.tokenStorage.hasToken()) {
      return;
    }

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
    } catch {
      this.tokenStorage.clearTokens();
    }
  }
}
