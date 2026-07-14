import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';

type PaymentStatus = 'success' | 'failure' | 'pending';

interface StatusConfig {
  icon: string;
  iconColor: string;
  iconBg: string;
  title: string;
  subtitle: string;
  badgeClass: string;
  badgeText: string;
}

@Component({
  selector: 'app-pago-resultado',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pago-resultado.component.html',
  styleUrls: ['./pago-resultado.component.css']
})
export class PagoResultadoComponent implements OnInit {

  status = signal<PaymentStatus>('pending');
  paymentId = signal<string | null>(null);
  reservaId = signal<string | null>(null);

  readonly statusConfig: Record<PaymentStatus, StatusConfig> = {
    success: {
      icon: 'fas fa-check-circle',
      iconColor: '#059669',
      iconBg: 'rgba(5, 150, 105, 0.1)',
      title: '¡Pago Exitoso!',
      subtitle: 'Tu reserva ha sido confirmada. Recibirás los detalles por WhatsApp.',
      badgeClass: 'badge-success',
      badgeText: 'Confirmado'
    },
    failure: {
      icon: 'fas fa-times-circle',
      iconColor: '#dc2626',
      iconBg: 'rgba(220, 38, 38, 0.1)',
      title: 'Pago No Procesado',
      subtitle: 'No pudimos procesar tu pago. Puedes intentarlo de nuevo desde tus reservas.',
      badgeClass: 'badge-danger',
      badgeText: 'Fallido'
    },
    pending: {
      icon: 'fas fa-clock',
      iconColor: '#d97706',
      iconBg: 'rgba(217, 119, 6, 0.1)',
      title: 'Pago Pendiente',
      subtitle: 'Tu pago está siendo procesado. Te notificaremos cuando sea confirmado.',
      badgeClass: 'badge-warning',
      badgeText: 'En proceso'
    }
  };

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const payment = params['payment'];
      if (payment === 'success') this.status.set('success');
      else if (payment === 'failure') this.status.set('failure');
      else this.status.set('pending');

      this.paymentId.set(params['payment_id'] ?? null);
      this.reservaId.set(params['external_reference'] ?? null);
    });
  }

  get config(): StatusConfig {
    return this.statusConfig[this.status()];
  }
}
