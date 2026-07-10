import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Lugar } from '../../shared/models/lugar.model';

@Injectable({ providedIn: 'root' })
export class LugaresService {
  private readonly baseUrl = `${environment.apiUrl}/lugares`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Lugar[]> {
    return this.http.get<Lugar[]>(this.baseUrl);
  }

  getById(id: number): Observable<Lugar> {
    return this.http.get<Lugar>(`${this.baseUrl}/${id}`);
  }

  create(lugar: Omit<Lugar, 'idLugar'>): Observable<Lugar> {
    return this.http.post<Lugar>(this.baseUrl, lugar);
  }

  update(id: number, lugar: Omit<Lugar, 'idLugar'>): Observable<Lugar> {
    return this.http.put<Lugar>(`${this.baseUrl}/${id}`, lugar);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
