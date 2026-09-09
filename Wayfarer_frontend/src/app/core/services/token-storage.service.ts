import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {

  private readonly ACCESS_TOKEN_KEY = 'access_token';

  private readonly accessTokenSignal = signal<string | null>(
    localStorage.getItem(this.ACCESS_TOKEN_KEY)
  );

  getAccessToken(): string | null {
    return this.accessTokenSignal();
  }

  saveAccessToken(accessToken: string): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    this.accessTokenSignal.set(accessToken);
  }

  clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    this.accessTokenSignal.set(null);
  }

  hasAccessToken(): boolean {
    return this.accessTokenSignal() !== null;
  }
}
