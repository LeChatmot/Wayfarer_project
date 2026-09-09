import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse, UserUpdateRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {

  constructor(private readonly http: HttpClient) {}

  getCurrentUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${environment.apiUrl}/users/me`);
  }

  updateCurrentUser(request: UserUpdateRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${environment.apiUrl}/users/me`, request);
  }

  deleteCurrentUser(): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/users/me`)
  }
}
