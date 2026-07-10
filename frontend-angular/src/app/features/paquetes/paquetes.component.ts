import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PaquetesService } from '../../core/services/paquetes.service';
import { InventarioService } from '../../core/services/inventario.service';
import { AuthService } from '../../core/auth/auth.service';
import { Paquete } from '../../shared/models/paquete.model';
import { Inventario } from '../../shared/models/inventario.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-paquetes',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, LoadingSpinnerComponent],
  templateUrl: './paquetes.component.html'
})
export class PaquetesComponent implements OnInit {
  private paquetesService = inject(PaquetesService);
  private inventarioService = inject(InventarioService);
  auth = inject(AuthService);

  allPaquetes = signal<Paquete[]>([]);
  inventarios = signal<Inventario[]>([]);
  loading = signal(false);

  searchText = signal('');
  filtroEstado = signal('activo');

  selectedPaquete = signal<Paquete | null>(null);
  selectedInventario = signal<Inventario | null>(null);
  showAuthModal = signal(false);
  showReservaModal = signal(false);

  filteredPaquetes = computed(() => {
    const text = this.searchText().toLowerCase();
    const estado = this.filtroEstado();
    return this.allPaquetes().filter(p => {
      const matchText = !text || p.nombrePaquete.toLowerCase().includes(text)
        || p.lugar?.ciudad?.toLowerCase().includes(text)
        || p.descripcion?.toLowerCase().includes(text);
      const matchEstado = !estado || p.estado === estado;
      return matchText && matchEstado;
    });
  });

  ngOnInit(): void {
    this.loading.set(true);
    this.paquetesService.getActivos().subscribe({
      next: (data) => { this.allPaquetes.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
    this.inventarioService.getDisponible().subscribe({
      next: (data) => this.inventarios.set(data)
    });
  }

  getInventarioByPaquete(idPaquete: number): Inventario[] {
    return this.inventarios().filter(inv => inv.idPaquete === idPaquete && inv.cupoDisponible > 0);
  }

  getCuposTotal(idPaquete: number): number {
    return this.getInventarioByPaquete(idPaquete).reduce((s, i) => s + i.cupoDisponible, 0);
  }

  getImageUrl(paquete: Paquete): string {
    const nombre = paquete.nombrePaquete?.toLowerCase() || '';
    if (nombre.includes('machu')) return 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Machu_Picchu%2C_Peru.jpg/800px-Machu_Picchu%2C_Peru.jpg';
    if (nombre.includes('vinicunca') || nombre.includes('color')) return 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Rainbow_Mountain_Peru.jpg/800px-Rainbow_Mountain_Peru.jpg';
    if (nombre.includes('valle')) return 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Pisac01.jpg/800px-Pisac01.jpg';
    return 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Cusco.jpg/800px-Cusco.jpg';
  }

  openModal(paquete: Paquete): void {
    this.selectedPaquete.set(paquete);
    const invs = this.getInventarioByPaquete(paquete.idPaquete);
    this.selectedInventario.set(invs[0] || null);
  }

  closeModal(): void {
    this.selectedPaquete.set(null);
    this.selectedInventario.set(null);
  }

  reservar(): void {
    if (!this.auth.isAuthenticated()) {
      this.showAuthModal.set(true);
      return;
    }
    this.showReservaModal.set(true);
  }
}
