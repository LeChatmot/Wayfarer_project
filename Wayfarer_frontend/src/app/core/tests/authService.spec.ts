import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { AuthService } from '../services/auth.service';
import { LOCAL_STORAGE } from '../tokens/local-storage.token';
import mockLocalStorage from '../mock/mockLocalStorage';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    mockLocalStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: LOCAL_STORAGE, useValue: mockLocalStorage },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should not be authenticated by default', () => {
    expect(service.isAuthenticated()).toBeFalsy();
  });

  it('should store token and set isAuthenticated to true on login', () => {
    service.login({ email: 'test@test.com', password: 'password' }).subscribe();

    const req = httpMock.expectOne(req => req.url.endsWith('/auth/login'));
    req.flush({ token: 'fake-jwt-token' });

    expect(service.getToken()).toBe('fake-jwt-token');
    expect(service.isAuthenticated()).toBeTruthy();
  });

  it('should clear token on logout', () => {
    mockLocalStorage.setItem('wayfarer_token', 'existing-token');

    service.logout();

    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBeFalsy();
  });
});
