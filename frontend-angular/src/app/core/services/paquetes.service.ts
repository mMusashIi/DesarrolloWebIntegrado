import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Paquete, PaqueteRequest, PaqueteSearchParams } from '../../shared/models/paquete.model';

@Injectable({ providedIn: 'root' })
export class PaquetesService {
  private readonly baseUrl = `${environment.apiUrl}/paquetes`;

  constructor(private http: HttpClient) {}

  getActivos(): Observable<Paquete[]> {
    return this.http.get<Paquete[]>(`${this.baseUrl}/activos`);
  }

  getAll(): Observable<Paquete[]> {
    return this.http.get<Paquete[]>(this.baseUrl);
  }

  getById(id: number): Observable<Paquete> {
    return this.http.get<Paquete>(`${this.baseUrl}/${id}`);
  }

  search(params: PaqueteSearchParams): Observable<Paquete[]> {
    let httpParams = new HttpParams();
    if (params.nombre) httpParams = httpParams.set('nombre', params.nombre);
    if (params.precioMin != null) httpParams = httpParams.set('precioMin', params.precioMin.toString());
    if (params.precioMax != null) httpParams = httpParams.set('precioMax', params.precioMax.toString());
    if (params.estado) httpParams = httpParams.set('estado', params.estado);
    return this.http.get<Paquete[]>(`${this.baseUrl}/public/search`, { params: httpParams });
  }

  create(paquete: PaqueteRequest): Observable<Paquete> {
    return this.http.post<Paquete>(this.baseUrl, paquete);
  }

  update(id: number, paquete: PaqueteRequest): Observable<Paquete> {
    return this.http.put<Paquete>(`${this.baseUrl}/${id}`, paquete);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
