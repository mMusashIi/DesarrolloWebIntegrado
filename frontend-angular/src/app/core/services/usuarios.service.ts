import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Usuario } from '../../shared/models/usuario.model';
import { ApiResponse, unwrapData } from './api-response';

@Injectable({ providedIn: 'root' })
export class UsuariosService {
  private readonly baseUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Usuario[]> {
    return this.http.get<ApiResponse<Usuario[]>>(this.baseUrl).pipe(map(unwrapData));
  }

  getById(id: number): Observable<Usuario> {
    return this.http.get<ApiResponse<Usuario>>(`${this.baseUrl}/${id}`).pipe(map(unwrapData));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }
}
