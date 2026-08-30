import { APP_INITIALIZER, FactoryProvider } from '@angular/core';
import {AuthInitializerService} from '../services/authInitializer.service';

export function initializeAuth(authInitializer: AuthInitializerService): () => Promise<void> {
  return () => authInitializer.initialize();
}

export const authInitializerProvider: FactoryProvider = {
  provide: APP_INITIALIZER,
  useFactory: initializeAuth,
  deps: [AuthInitializerService],
  multi: true
};
