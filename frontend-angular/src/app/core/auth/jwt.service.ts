import { Injectable } from '@angular/core';
import { Usuario } from '../../shared/models/usuario.model';

@Injectable({ providedIn: 'root' })
export class JwtService {
  private readonly TOKEN_KEY = 'buganvilla_token';
  private readonly USER_KEY = 'buganvilla_user';

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUser(): Usuario | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? JSON.parse(raw) as Usuario : null;
  }

  setSession(token: string, user: Usuario): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  hasToken(): boolean {
    return !!this.getToken();
  }
}
