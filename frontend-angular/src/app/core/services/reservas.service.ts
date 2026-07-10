import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Reserva, ReservaRequest, PreferenciaMP } from '../../shared/models/reserva.model';

@Injectable({ providedIn: 'root' })
export class ReservasService {
  private readonly baseUrl = `${environment.apiUrl}/reservas`;
  private readonly mpUrl = `${environment.apiUrl}/mercadopago`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(this.baseUrl);
  }

  getMisReservas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.baseUrl}/mis-reservas`);
  }

  getById(id: number): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.baseUrl}/${id}`);
  }

  create(request: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.baseUrl, request);
  }

  cancelar(id: number): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.baseUrl}/${id}/cancelar`, {});
  }

  crearPreferenciaMP(reservaId: number): Observable<PreferenciaMP> {
    return this.http.post<PreferenciaMP>(`${this.mpUrl}/crear-preferencia`, { reservaId });
  }
}
