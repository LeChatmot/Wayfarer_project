import { Injectable } from '@angular/core';
import { HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {HikePathResponse, HikeResponse, HikeSaveRequest, HikeSearchCriteria, HikeUpdateRequest} from "../models/hike.models";
import {PageResponse} from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class HikeService {

  constructor(private readonly http: HttpClient) {}

  create(request: HikeSaveRequest): Observable<HikeResponse> {
    return this.http.post<HikeResponse>(`${environment.apiUrl}/hikes`, request);
  }

  addFavorite(hikeId: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/hikes/${hikeId}/favorite`, {});
  }

  removeFavorite(hikeId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/hikes/${hikeId}/favorite`);
  }

  getById(id: number): Observable<HikeResponse> {
    return this.http.get<HikeResponse>(`${environment.apiPublicUrl}/hikes/${id}`);
  }

  update(id: number, request: HikeUpdateRequest): Observable<HikeResponse> {
    return this.http.patch<HikeResponse>(`${environment.apiUrl}/hikes/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/hikes/${id}/delete`);
  }

  search(criteria: HikeSearchCriteria & { page?: number }): Observable<PageResponse<HikeResponse>> {
    let params = new HttpParams();

    Object.entries(criteria).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        params = params.set(key, String(value));
      }
    });

    params = params.set('page', String(criteria.page ?? 0));
    params = params.set('size', '12');

    return this.http.get<PageResponse<HikeResponse>>(`${environment.apiPublicUrl}/hikes/search`, { params });
  }

  isCreator(hikeId: number): Observable<boolean>{
    return this.http.get<boolean>(`${environment.apiPublicUrl}/hikes/${hikeId}/isCreator`)
  }

  getMyHikes(page: number, size: number): Observable<PageResponse<HikeResponse>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<HikeResponse>>(`${environment.apiUrl}/hikes/mines`, { params });
  }

  getFavorites(page: number, size: number): Observable<PageResponse<HikeResponse>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http.get<PageResponse<HikeResponse>>(`${environment.apiUrl}/hikes/favorites`, { params });
  }

  getHikePath(hikeId: number): Observable<HikePathResponse>{
    return this.http.get<HikePathResponse>(`${environment.apiPublicUrl}/hikes/${hikeId}/getPath`)
  }
}
