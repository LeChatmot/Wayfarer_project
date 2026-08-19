import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface Location {
  name: string;
  latitude: number;
  longitude: number;
  displayName: string;
  type: string;
}

export interface LocationResponse {
  locations: Location[];
  count: number;
  query: string;
}

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private readonly nominatimUrl = 'https://nominatim.openstreetmap.org/search';

  constructor(private readonly http: HttpClient) {}

  searchLocations(query: string, limit: number = 10): Observable<Location[]> {
    const params = {
      q: query,
      format: 'json',
      limit: limit.toString(),
      'accept-language': 'fr'
    };

    return this.http.get<any[]>(this.nominatimUrl, { params }).pipe(
      catchError(error => {
        console.error('Erreur lors de la recherche de lieux:', error);
        return [];
      }),
      map(response => {
        if (!response || response.length === 0) {
          return [];
        }

        return response.map(item => ({
          name: item.display_name.split(',')[0],
          latitude: Number.parseFloat(item.lat),
          longitude: Number.parseFloat(item.lon),
          displayName: item.display_name,
          type: item.type || 'lieu'
        }));
      })
    );
  }
}
