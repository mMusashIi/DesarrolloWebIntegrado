import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservasService } from '../../../core/services/reservas.service';
import { Reserva } from '../../../shared/models/reserva.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-admin-reservas',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  templateUrl: './admin-reservas.component.html'
})
export class AdminReservasComponent implements OnInit {
  private reservasService = inject(ReservasService);

  reservas = signal<Reserva[]>([]);
  loading = signal(true);
  canceling = signal<number | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.reservasService.getAll().subscribe({
      next: (data) => { this.reservas.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  cancelar(id: number): void {
    if (!confirm('¿Cancelar esta reserva? Se restaurarán los cupos.')) return;
    this.canceling.set(id);
    this.reservasService.cancelar(id).subscribe({
      next: () => { this.canceling.set(null); this.load(); },
      error: (err) => {
        this.canceling.set(null);
        alert(err?.error?.message || 'Error al cancelar.');
      }
    });
  }

  getBadgeStyle(estado: string): { background: string; color: string } {
    switch (estado) {
      case 'confirmada': return { background: '#d1fae5', color: '#065f46' };
      case 'pendiente': return { background: '#fef3c7', color: '#92400e' };
      case 'cancelada': return { background: '#fee2e2', color: '#991b1b' };
      default: return { background: '#e5e7eb', color: '#374151' };
    }
  }
}
