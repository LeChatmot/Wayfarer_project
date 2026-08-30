import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const accessToken = tokenStorage.getAccessToken();

  const isAuthUrl = req.url.includes('/auth/login')
    || req.url.includes('/auth/register')
    || req.url.includes('/auth/refresh');

  if (accessToken && !isAuthUrl) {
    const cloned = req.clone({
      setHeaders: { Authorization: `Bearer ${accessToken}` }
    });
    return next(cloned);
  }

  return next(req);
};
