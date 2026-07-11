import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Lugar } from '../../shared/models/lugar.model';
import { ApiResponse, unwrapData } from './api-response';

@Injectable({ providedIn: 'root' })
export class LugaresService {
  private readonly baseUrl = `${environment.apiUrl}/lugares`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Lugar[]> {
    return this.http.get<ApiResponse<Lugar[]>>(this.baseUrl).pipe(map(unwrapData));
  }

  getById(id: number): Observable<Lugar> {
    return this.http.get<ApiResponse<Lugar>>(`${this.baseUrl}/${id}`).pipe(map(unwrapData));
  }

  create(lugar: Omit<Lugar, 'idLugar'>): Observable<Lugar> {
    return this.http.post<ApiResponse<Lugar>>(this.baseUrl, lugar).pipe(map(unwrapData));
  }

  update(id: number, lugar: Omit<Lugar, 'idLugar'>): Observable<Lugar> {
    return this.http.put<ApiResponse<Lugar>>(`${this.baseUrl}/${id}`, lugar).pipe(map(unwrapData));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }
}
