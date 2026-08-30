import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthUrl = req.url.includes('/auth/login')
        || req.url.includes('/auth/register')
        || req.url.includes('/auth/refresh');

      if (error.status !== 401 || isAuthUrl) {
        return throwError(() => error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        refreshTokenSubject.next(null);

        return authService.refreshToken().pipe(
          switchMap(response => {
            isRefreshing = false;
            refreshTokenSubject.next(response.accessToken);

            const cloned = req.clone({
              setHeaders: { Authorization: `Bearer ${response.accessToken}` }
            });
            return next(cloned);
          }),
          catchError(refreshError => {
            isRefreshing = false;
            tokenStorage.clearTokens();
            router.navigate(['/login']);
            return throwError(() => refreshError);
          })
        );
      }

      return refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => {
          const cloned = req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
          });
          return next(cloned);
        })
      );
    })
  );
};
