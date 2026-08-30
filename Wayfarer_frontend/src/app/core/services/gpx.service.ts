import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, from } from 'rxjs';
import { environment } from '../../../environments/environment';
import {GpxPreview, HikeSaveRequest} from '../models/hike.models';
import {parseGpxPreview} from '../utils/gpx-preview.utils';
import {HikeResponse} from './hike.service';

@Injectable({ providedIn: 'root' })
export class GpxService {

  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/hikes`;

  preview(file: File): Observable<GpxPreview> {
    return from(
      file.text().then(content => {
        const preview = parseGpxPreview(content);
        if (!preview) {
          throw new Error('Fichier GPX invalide ou sans trace');
        }
        return preview;
      })
    );
  }

  save(request: HikeSaveRequest): Observable<HikeResponse> {
    return this.http.post<HikeResponse>(this.apiUrl, request);
  }

  findNearby(latitude: number, longitude: number, radiusMeters: number): Observable<HikeResponse[]> {
    return this.http.get<HikeResponse[]>(`${this.apiUrl}/nearby`, {
      params: { latitude, longitude, radius: radiusMeters }
    });
  }

  toggleFavorite(hikeId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${hikeId}/favorite`, {});
  }
}
