import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, TokenValidationResponse } from '../models/auth.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(
    private readonly http: HttpClient,
    private readonly tokenStorage: TokenStorageService
  ) {}

  login(email: string, password: string): Observable<AuthResponse> {
    const request: LoginRequest = { email, password };
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/login`,
      request,
      { withCredentials: true }
    ).pipe(
      tap(response => this.tokenStorage.saveAccessToken(response.accessToken))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/register`,
      request,
      { withCredentials: true }
    ).pipe(
      tap(response => this.tokenStorage.saveAccessToken(response.accessToken))
    );
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${environment.apiUrl}/auth/refresh`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(response => this.tokenStorage.saveAccessToken(response.accessToken))
    );
  }

  validateToken(): Observable<TokenValidationResponse> {
    const accessToken = this.tokenStorage.getAccessToken();
    return this.http.get<TokenValidationResponse>(
      `${environment.apiUrl}/auth/validate`,
      {
        headers: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
        withCredentials: true
      }
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(
      `${environment.apiUrl}/auth/logout`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(() => this.tokenStorage.clearTokens())
    );
  }

  isAuthenticated(): boolean {
    return this.tokenStorage.hasAccessToken();
  }

  isAdmin(): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiUrl}/auth/isAdmin`)
  }
}
