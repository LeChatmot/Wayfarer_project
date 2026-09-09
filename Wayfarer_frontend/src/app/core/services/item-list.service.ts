import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ItemList} from '../models/item-list.model';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ItemListService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/item-lists`;

  getMyLists(): Observable<ItemList[]> {
    return this.http.get<ItemList[]>(this.baseUrl);
  }

  createList(name: string): Observable<ItemList> {
    return this.http.post<ItemList>(this.baseUrl, { name });
  }

  addItem(listId: number, name: string, quantity: number | null): Observable<ItemList> {
    return this.http.post<ItemList>(`${this.baseUrl}/${listId}/items`, { name, quantity });
  }

  removeItem(listId: number, itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${listId}/items/${itemId}`);
  }

  updateItem(listId: number, itemId: number, name: string, quantity: number | null): Observable<ItemList> {
    return this.http.put<ItemList>(`${this.baseUrl}/${listId}/items/${itemId}`, { name, quantity });
  }

  deleteList(listId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${listId}`);
  }
}
