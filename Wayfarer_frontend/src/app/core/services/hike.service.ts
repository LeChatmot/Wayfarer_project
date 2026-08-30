import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';

export interface HikeCreateRequest {
  name: string;
  description: string;
  backToStart: boolean;
  gpxContent: string;
}

export interface HikeResponse {
  id: number;
  name: string;
  description: string;
  creatorUsername: string;
  distanceMeters: number;
  elevationGain: number;
  elevationLoss: number;
  durationSeconds: number | null;
  backToStart: boolean;
  favorite: boolean;
}

@Injectable({ providedIn: 'root' })
export class HikeService {

  constructor(private readonly http: HttpClient) {}

  create(request: HikeCreateRequest): Observable<HikeResponse> {
    return this.http.post<HikeResponse>(`${environment.apiUrl}/hikes`, request);
  }

  addFavorite(hikeId: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/hikes/${hikeId}/favorite`, {});
  }

  removeFavorite(hikeId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/hikes/${hikeId}/favorite`);
  }
}
