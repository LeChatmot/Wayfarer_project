import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Hike {
  id: number;
  name: string;
  description?: string;
  distance: number;
  duration: number;
  difficulty: 'EASY' | 'MODERATE' | 'HARD' | 'EXPERT';
  gpxData?: string;
  elevationGain?: number;
  elevationLoss?: number;
  createdAt: string;
}

export interface HikeCreateRequest {
  name: string;
  description?: string;
  gpxData: string;
  distance: number;
  duration: number;
  difficulty: string;
  elevationGain: number;
  elevationLoss: number;
}

@Injectable({ providedIn: 'root' })
export class HikeService {
  private apiUrl = '/api/hikes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Hike[]> {
    return this.http.get<Hike[]>(this.apiUrl);
  }

  getById(id: number): Observable<Hike> {
    return this.http.get<Hike>(`${this.apiUrl}/${id}`);
  }

  create(hike: HikeCreateRequest): Observable<Hike> {
    return this.http.post<Hike>(this.apiUrl, hike);
  }

  uploadGpx(file: File): Observable<{ distance: number; duration: number; elevationGain: number; elevationLoss: number; gpxData: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.apiUrl}/upload-gpx`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
