import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Reserva, ReservaRequest, PreferenciaMP } from '../../shared/models/reserva.model';
import { ApiResponse, unwrapData } from './api-response';

@Injectable({ providedIn: 'root' })
export class ReservasService {
  private readonly baseUrl = `${environment.apiUrl}/reservas`;
  private readonly mpUrl = `${environment.apiUrl}/mercadopago`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Reserva[]> {
    return this.http.get<ApiResponse<Reserva[]>>(this.baseUrl).pipe(map(unwrapData));
  }

  getMisReservas(): Observable<Reserva[]> {
    return this.http.get<ApiResponse<Reserva[]>>(`${this.baseUrl}/mis-reservas`).pipe(map(unwrapData));
  }

  getById(id: number): Observable<Reserva> {
    return this.http.get<ApiResponse<Reserva>>(`${this.baseUrl}/${id}`).pipe(map(unwrapData));
  }

  create(request: ReservaRequest): Observable<Reserva> {
    return this.http.post<ApiResponse<Reserva>>(this.baseUrl, request).pipe(map(unwrapData));
  }

  cancelar(id: number): Observable<Reserva> {
    return this.http.put<ApiResponse<Reserva>>(`${this.baseUrl}/${id}/cancelar`, {}).pipe(map(unwrapData));
  }

  crearPreferenciaMP(reservaId: number, monto = 0, descripcion = 'Reserva Buganvilla Tours', cantidad = 1): Observable<PreferenciaMP> {
    return this.http.post<ApiResponse<PreferenciaMP>>(
      `${this.mpUrl}/crear-preferencia`,
      { reservaId, monto, descripcion, cantidad }
    ).pipe(map(unwrapData));
  }
}
