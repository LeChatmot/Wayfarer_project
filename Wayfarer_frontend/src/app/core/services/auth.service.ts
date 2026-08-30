import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, TokenValidationResponse} from '../models/auth.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorageService
  ) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap(response => this.tokenStorage.saveTokens(response.accessToken, response.refreshToken))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, request).pipe(
      tap(response => this.tokenStorage.saveTokens(response.accessToken, response.refreshToken))
    );
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.tokenStorage.getRefreshToken();
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/refresh`, { refreshToken }).pipe(
      tap(response => this.tokenStorage.saveTokens(response.accessToken, response.refreshToken))
    );
  }

  validateToken(): Observable<TokenValidationResponse> {
    const accessToken = this.tokenStorage.getAccessToken();
    return this.http.post<TokenValidationResponse>(`${environment.apiUrl}/auth/validate`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    });
  }

  logout(): void {
    const refreshToken = this.tokenStorage.getRefreshToken();
    if (refreshToken) {
      this.http.post(`${environment.apiUrl}/auth/logout`, { refreshToken }).subscribe();
    }
    this.tokenStorage.clearTokens();
  }

  isAuthenticated(): boolean {
    return this.tokenStorage.hasToken();
  }
}
