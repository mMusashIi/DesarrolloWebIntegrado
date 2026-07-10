import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DniResponse {
  numeroDocumento: string;
  nombre: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
}

@Injectable({ providedIn: 'root' })
export class ApisNetService {
  private readonly baseUrl = `${environment.apiUrl}/apis-net`;

  constructor(private http: HttpClient) {}

  consultarDni(dni: string): Observable<DniResponse> {
    return this.http.get<DniResponse>(`${this.baseUrl}/dni/${dni}`);
  }
}
