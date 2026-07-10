export interface Pago {
  idPago: number;
  idReserva: number;
  monto: number;
  metodo: string;
  estado: 'pendiente' | 'completado' | 'rechazado' | 'en_proceso';
  fechaPago?: string;
  mpStatus?: string;
}
