import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {MapProvidersModel} from '../models/map-providers.model';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class MapProvidersService{
  private readonly apiUrl = `${environment.apiPublicUrl}/map-provider`;

  constructor(private http: HttpClient) {}

  public getMapProvidersList(): Observable<MapProvidersModel[]>{
    return this.http.get<MapProvidersModel[]>(`${this.apiUrl}/list`);
  }

  public getPlanIGNMapProvier(): Observable<MapProvidersModel>{
    return this.http.get<MapProvidersModel>(`${this.apiUrl}/plan-ign`);
  }
}
