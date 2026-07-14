import { Lugar } from './lugar.model';

export interface Paquete {
  idPaquete: number;
  nombrePaquete: string;
  descripcion?: string;
  precioBase: number;
  duracionDias?: number;
  estado: 'activo' | 'inactivo';
  lugar?: Lugar;
  idLugar?: number;
  nombreLugar?: string;
  ciudadLugar?: string;
  imagenUrl?: string;
}

export interface PaqueteRequest {
  nombrePaquete: string;
  descripcion: string;
  precioBase: number;
  duracionDias: number;
  estado: string;
  idLugar: number;
  imagenUrl?: string;
}

export interface PaqueteSearchParams {
  nombre?: string;
  precioMin?: number;
  precioMax?: number;
  estado?: string;
}
