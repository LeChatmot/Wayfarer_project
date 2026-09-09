import {APP_INITIALIZER, ApplicationConfig, provideZonelessChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {errorInterceptor} from './core/interceptors/error.interceptor';
import {authInitializerProvider, initializeAuth} from './core/providers/AppInitializer';
import {AuthInitializerService} from './core/services/authInitializer.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      deps: [AuthInitializerService],
      multi: true
    },
  ]
};
