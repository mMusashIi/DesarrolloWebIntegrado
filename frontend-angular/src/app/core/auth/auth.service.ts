import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JwtService } from './jwt.service';
import { ApiResponse, unwrapData } from '../services/api-response';
import {
  Usuario,
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  ProfileUpdateRequest
} from '../../shared/models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  private _user = signal<Usuario | null>(null);

  readonly currentUser = this._user.asReadonly();
  readonly isAuthenticated = computed(() => !!this._user());
  readonly isAdmin = computed(() => {
    const user = this._user();
    return user?.rol?.toLowerCase() === 'admin';
  });

  constructor(
    private http: HttpClient,
    private jwt: JwtService,
    private router: Router
  ) {
    const stored = this.jwt.getUser();
    if (stored && this.jwt.hasToken()) {
      this._user.set(stored);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      map(unwrapData),
      tap((res) => this.jwt.setSession(res.token, res.usuario)),
      tap((res) => this._user.set(res.usuario))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/register`, data).pipe(
      map(unwrapData),
      tap((res) => this.jwt.setSession(res.token, res.usuario)),
      tap((res) => this._user.set(res.usuario))
    );
  }

  logout(): void {
    this.jwt.clearSession();
    this._user.set(null);
    this.router.navigate(['/']);
  }

  getProfile(): Observable<Usuario> {
    return this.http.get<ApiResponse<Usuario>>(`${this.apiUrl}/profile`).pipe(
      map(unwrapData),
      tap((user) => {
        this._user.set(user);
        const token = this.jwt.getToken();
        if (token) this.jwt.setSession(token, user);
      })
    );
  }

  updateProfile(data: ProfileUpdateRequest): Observable<Usuario> {
    return this.http.put<ApiResponse<Usuario>>(`${this.apiUrl}/profile`, data).pipe(
      map(unwrapData),
      tap(user => {
        this._user.set(user);
        const token = this.jwt.getToken();
        if (token) this.jwt.setSession(token, user);
      })
    );
  }
}
