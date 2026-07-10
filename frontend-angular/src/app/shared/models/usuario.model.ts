export interface Usuario {
  idUsuario: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  nacionalidad?: string;
  dni?: string;
  rol?: 'admin' | 'cliente';
  activo: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  telefono: string;
  nacionalidad: string;
}

export interface AuthResponse {
  token: string;
  usuario: Usuario;
}

export interface RegisterResponse {
  success: boolean;
  data: AuthResponse;
}
