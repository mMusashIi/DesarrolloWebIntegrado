import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PaquetesService } from '../../core/services/paquetes.service';
import { InventarioService } from '../../core/services/inventario.service';
import { Paquete } from '../../shared/models/paquete.model';
import { Inventario } from '../../shared/models/inventario.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, LoadingSpinnerComponent],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  private paquetesService = inject(PaquetesService);
  private inventarioService = inject(InventarioService);

  paquetes = signal<Paquete[]>([]);
  inventarios = signal<Inventario[]>([]);
  loading = signal(false);

  stats = [
    { icon: '🏆', value: '5+', label: 'Años de experiencia' },
    { icon: '🌍', value: '500+', label: 'Tours realizados' },
    { icon: '😊', value: '2,000+', label: 'Clientes felices' },
    { icon: '📍', value: '10+', label: 'Destinos' }
  ];

  testimonios = [
    {
      nombre: 'María García',
      texto: 'Increíble experiencia en Machu Picchu. El guía fue excelente y la organización perfecta.',
      estrellas: 5,
      origen: 'España'
    },
    {
      nombre: 'John Smith',
      texto: 'The trek to Vinicunca was breathtaking! Great team, highly recommended.',
      estrellas: 5,
      origen: 'Estados Unidos'
    },
    {
      nombre: 'Ana López',
      texto: 'El Tour Valle Sagrado superó todas mis expectativas. Volveré sin duda.',
      estrellas: 5,
      origen: 'Argentina'
    }
  ];

  ngOnInit(): void {
    this.loading.set(true);
    this.paquetesService.getActivos().subscribe({
      next: (data) => { this.paquetes.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
    this.inventarioService.getDisponible().subscribe({
      next: (data) => this.inventarios.set(data)
    });
  }

  getCuposPaquete(idPaquete: number): number {
    return this.inventarios()
      .filter(inv => inv.idPaquete === idPaquete)
      .reduce((sum, inv) => sum + inv.cupoDisponible, 0);
  }

  getImageUrl(paquete: Paquete): string {
    const nombre = paquete.nombrePaquete?.toLowerCase() || '';
    if (nombre.includes('machu')) return 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Machu_Picchu%2C_Peru.jpg/800px-Machu_Picchu%2C_Peru.jpg';
    if (nombre.includes('vinicunca') || nombre.includes('color')) return 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Rainbow_Mountain_Peru.jpg/800px-Rainbow_Mountain_Peru.jpg';
    if (nombre.includes('valle')) return 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Pisac01.jpg/800px-Pisac01.jpg';
    return 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Cusco.jpg/800px-Cusco.jpg';
  }
}
