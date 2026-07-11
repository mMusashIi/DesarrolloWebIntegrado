export interface Reserva {
  idReserva: number;
  idUsuario: number;
  nombreComprador?: string;
  telefonoComprador?: string;
  nombreCliente?: string;
  nombresViajeros?: string;
  telefonosViajeros?: string;
  dniCliente?: string;
  emailCliente?: string;
  telefonoCliente?: string;
  whatsappOptIn?: boolean;
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
  nombreComprador: string;
  telefonoComprador: string;
  nombreCliente: string;
  nombresViajeros: string;
  telefonosViajeros: string;
  dniCliente: string;
  emailCliente: string;
  telefonoCliente: string;
  whatsappOptIn: boolean;
  nombrePaquete: string;
  fechaViaje: string;
}

export interface PreferenciaMP {
  initPoint: string;
  pagoId: number;
}
