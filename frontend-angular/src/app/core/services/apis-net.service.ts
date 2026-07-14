import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApiResponse, unwrapData } from './api-response';

export interface DniResponse {
  numeroDocumento: string;
  nombres: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
}

@Injectable({ providedIn: 'root' })
export class ApisNetService {
  private readonly baseUrl = `${environment.apiUrl}/apis-net`;

  constructor(private http: HttpClient) {}

  consultarDni(dni: string): Observable<DniResponse> {
    return this.http
      .get<ApiResponse<DniResponse> | DniResponse>(`${this.baseUrl}/dni/${dni}`)
      .pipe(map(response => 'data' in response ? unwrapData(response) : response));
  }
}
