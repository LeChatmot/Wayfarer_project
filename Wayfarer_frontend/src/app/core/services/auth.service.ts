import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { LOCAL_STORAGE } from '../tokens/local-storage.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private readonly tokenKey = 'wayfarer_token';
  private readonly http = inject(HttpClient);
  private readonly storage = inject(LOCAL_STORAGE);

  readonly isAuthenticated = signal(this.hasToken());

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(response => this.storeToken(response.token))
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => this.storeToken(response.token))
    );
  }

  logout(): void {
    this.storage.removeItem(this.tokenKey);
    this.isAuthenticated.set(false);
  }

  getToken(): string | null {
    return this.storage.getItem(this.tokenKey);
  }

  private storeToken(token: string): void {
    this.storage.setItem(this.tokenKey, token);
    this.isAuthenticated.set(true);
  }

  private hasToken(): boolean {
    return !!this.storage.getItem(this.tokenKey);
  }
}
