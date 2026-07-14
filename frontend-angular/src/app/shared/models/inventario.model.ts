export interface Inventario {
  idInventario: number;
  idPaquete: number;
  nombrePaquete?: string;
  fechaSalida: string;
  fechaRetorno?: string;
  cupoTotal: number;
  cupoDisponible: number;
}

export interface InventarioRequest {
  idPaquete: number;
  fechaSalida: string;
  fechaRetorno?: string;
  cupoTotal: number;
  cupoDisponible: number;
}
