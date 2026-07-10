export interface Reserva {
  idReserva: number;
  idUsuario: number;
  nombreCliente?: string;
  idInventario: number;
  nombrePaquete?: string;
  fechaViaje?: string;
  cantidadPersonas: number;
  estado: 'pendiente' | 'confirmada' | 'cancelada';
  fechaReserva: string;
}

export interface ReservaRequest {
  idInventario: number;
  cantidadPersonas: number;
}

export interface PreferenciaMP {
  initPoint: string;
  pagoId: number;
}
