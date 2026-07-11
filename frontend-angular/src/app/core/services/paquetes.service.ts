import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Paquete, PaqueteRequest, PaqueteSearchParams } from '../../shared/models/paquete.model';
import { ApiResponse, unwrapData } from './api-response';

@Injectable({ providedIn: 'root' })
export class PaquetesService {
  private readonly baseUrl = `${environment.apiUrl}/paquetes`;

  constructor(private http: HttpClient) {}

  getActivos(): Observable<Paquete[]> {
    return this.http.get<ApiResponse<Paquete[]>>(`${this.baseUrl}/activos`).pipe(map(r => this.normalizeAll(unwrapData(r))));
  }

  getAll(): Observable<Paquete[]> {
    return this.http.get<ApiResponse<Paquete[]>>(this.baseUrl).pipe(map(r => this.normalizeAll(unwrapData(r))));
  }

  getById(id: number): Observable<Paquete> {
    return this.http.get<ApiResponse<Paquete>>(`${this.baseUrl}/${id}`).pipe(map(r => this.normalize(unwrapData(r))));
  }

  search(params: PaqueteSearchParams): Observable<Paquete[]> {
    let httpParams = new HttpParams();
    if (params.nombre) httpParams = httpParams.set('nombre', params.nombre);
    if (params.precioMin != null) httpParams = httpParams.set('precioMin', params.precioMin.toString());
    if (params.precioMax != null) httpParams = httpParams.set('precioMax', params.precioMax.toString());
    if (params.estado) httpParams = httpParams.set('estado', params.estado);
    return this.http.get<ApiResponse<Paquete[]>>(`${this.baseUrl}/public/search`, { params: httpParams })
      .pipe(map(r => this.normalizeAll(unwrapData(r))));
  }

  create(paquete: PaqueteRequest): Observable<Paquete> {
    return this.http.post<ApiResponse<Paquete>>(this.baseUrl, paquete).pipe(map(r => this.normalize(unwrapData(r))));
  }

  update(id: number, paquete: PaqueteRequest): Observable<Paquete> {
    return this.http.put<ApiResponse<Paquete>>(`${this.baseUrl}/${id}`, paquete).pipe(map(r => this.normalize(unwrapData(r))));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }

  private normalizeAll(paquetes: Paquete[]): Paquete[] {
    return (paquetes || []).map(paquete => this.normalize(paquete));
  }

  private normalize(paquete: Paquete): Paquete {
    if (!paquete || paquete.lugar || !paquete.idLugar) return paquete;
    return {
      ...paquete,
      lugar: {
        idLugar: paquete.idLugar,
        nombreLugar: paquete.nombreLugar || '',
        ciudad: paquete.ciudadLugar || '',
        descripcion: ''
      }
    };
  }
}
