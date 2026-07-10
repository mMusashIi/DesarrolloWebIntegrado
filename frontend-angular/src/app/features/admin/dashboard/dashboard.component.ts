import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReservasService } from '../../../core/services/reservas.service';
import { PaquetesService } from '../../../core/services/paquetes.service';
import { UsuariosService } from '../../../core/services/usuarios.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, LoadingSpinnerComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private reservasService = inject(ReservasService);
  private paquetesService = inject(PaquetesService);
  private usuariosService = inject(UsuariosService);

  loading = signal(true);
  totalReservas = signal(0);
  totalPaquetes = signal(0);
  totalUsuarios = signal(0);
  reservasRecientes = signal<any[]>([]);

  ngOnInit(): void {
    let loaded = 0;
    const checkDone = () => { if (++loaded === 3) this.loading.set(false); };

    this.reservasService.getAll().subscribe({
      next: (data) => {
        this.totalReservas.set(data.length);
        this.reservasRecientes.set(data.slice(0, 5));
        checkDone();
      },
      error: () => checkDone()
    });

    this.paquetesService.getAll().subscribe({
      next: (data) => { this.totalPaquetes.set(data.length); checkDone(); },
      error: () => checkDone()
    });

    this.usuariosService.getAll().subscribe({
      next: (data) => { this.totalUsuarios.set(data.length); checkDone(); },
      error: () => checkDone()
    });
  }
}
