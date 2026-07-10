import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Inventario, InventarioRequest } from '../../shared/models/inventario.model';

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private readonly baseUrl = `${environment.apiUrl}/inventario`;

  constructor(private http: HttpClient) {}

  getDisponible(): Observable<Inventario[]> {
    return this.http.get<Inventario[]>(`${this.baseUrl}/disponible`);
  }

  getAll(): Observable<Inventario[]> {
    return this.http.get<Inventario[]>(this.baseUrl);
  }

  getByPaquete(idPaquete: number): Observable<Inventario[]> {
    return this.http.get<Inventario[]>(`${this.baseUrl}/paquete/${idPaquete}`);
  }

  getDisponibleByPaquete(idPaquete: number): Observable<Inventario[]> {
    return this.http.get<Inventario[]>(`${this.baseUrl}/paquete/${idPaquete}/disponible`);
  }

  getProximasSalidas(): Observable<Inventario[]> {
    return this.http.get<Inventario[]>(`${this.baseUrl}/proximas-salidas`);
  }

  create(inventario: InventarioRequest): Observable<Inventario> {
    return this.http.post<Inventario>(this.baseUrl, inventario);
  }

  update(id: number, inventario: InventarioRequest): Observable<Inventario> {
    return this.http.put<Inventario>(`${this.baseUrl}/${id}`, inventario);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
