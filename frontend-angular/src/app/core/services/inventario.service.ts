import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Inventario, InventarioRequest } from '../../shared/models/inventario.model';
import { ApiResponse, unwrapData } from './api-response';

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private readonly baseUrl = `${environment.apiUrl}/inventario`;

  constructor(private http: HttpClient) {}

  getDisponible(): Observable<Inventario[]> {
    return this.http.get<ApiResponse<Inventario[]>>(`${this.baseUrl}/disponible`).pipe(map(unwrapData));
  }

  getAll(): Observable<Inventario[]> {
    return this.http.get<ApiResponse<Inventario[]>>(this.baseUrl).pipe(map(unwrapData));
  }

  getByPaquete(idPaquete: number): Observable<Inventario[]> {
    return this.http.get<ApiResponse<Inventario[]>>(`${this.baseUrl}/paquete/${idPaquete}`).pipe(map(unwrapData));
  }

  getDisponibleByPaquete(idPaquete: number): Observable<Inventario[]> {
    return this.http.get<ApiResponse<Inventario[]>>(`${this.baseUrl}/paquete/${idPaquete}/disponible`).pipe(map(unwrapData));
  }

  getProximasSalidas(): Observable<Inventario[]> {
    return this.http.get<ApiResponse<Inventario[]>>(`${this.baseUrl}/proximas-salidas`).pipe(map(unwrapData));
  }

  create(inventario: InventarioRequest): Observable<Inventario> {
    return this.http.post<ApiResponse<Inventario>>(this.baseUrl, inventario).pipe(map(unwrapData));
  }

  update(id: number, inventario: InventarioRequest): Observable<Inventario> {
    return this.http.put<ApiResponse<Inventario>>(`${this.baseUrl}/${id}`, inventario).pipe(map(unwrapData));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }

  getContextoPago(id: number): Observable<Inventario> {
    return this.http.get<Inventario>(`${this.baseUrl}/${id}/contexto-pago`);
  }
}
