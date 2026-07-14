import { AfterViewInit, Component, ElementRef, OnInit, ViewChild, signal, inject } from '@angular/core';
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
export class HomeComponent implements OnInit, AfterViewInit {
  @ViewChild('heroVideo') heroVideo?: ElementRef<HTMLVideoElement>;
  private paquetesService = inject(PaquetesService);
  private inventarioService = inject(InventarioService);

  paquetes = signal<Paquete[]>([]);
  inventarios = signal<Inventario[]>([]);
  loading = signal(false);

  stats = [
    { value: '1500', label: 'Clientes Satisfechos' },
    { value: '50', label: 'Tours Realizados' },
    { value: '+10', label: 'Destinos' },
    { value: '+20', label: 'Años de Experiencia' }
  ];

  testimonios = [
    {
      nombre: 'María García',
      texto: 'Increíble experiencia en las Líneas de Nazca. El guía fue excelente y la organización perfecta. Sin duda lo mejor de Ica.',
      estrellas: 5,
      origen: 'Lima, Perú'
    },
    {
      nombre: 'Carlos Mendoza',
      texto: 'Las Islas Ballestas superaron todas mis expectativas. Ver los lobos marinos y los pingüinos fue espectacular. ¡Totalmente recomendado!',
      estrellas: 5,
      origen: 'Arequipa, Perú'
    },
    {
      nombre: 'Ana Torres',
      texto: 'El tour a Huacachina fue mágico. El sandboarding y el paseo en buggy al atardecer fue una experiencia que nunca olvidaré.',
      estrellas: 5,
      origen: 'España'
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

  ngAfterViewInit(): void {
    const video = this.heroVideo?.nativeElement;
    if (!video) return;
    video.muted = true;
    video.defaultMuted = true;
    video.loop = true;
    video.play().catch(() => {
      const resume = () => video.play().catch(() => undefined);
      document.addEventListener('click', resume, { once: true });
      document.addEventListener('touchstart', resume, { once: true });
    });
  }

  getCuposPaquete(idPaquete: number): number {
    return this.inventarios()
      .filter(inv => inv.idPaquete === idPaquete)
      .reduce((sum, inv) => sum + inv.cupoDisponible, 0);
  }

  getImageUrl(paquete: Paquete): string {
    if (paquete.imagenUrl) return paquete.imagenUrl;
    const nombre = paquete.nombrePaquete?.toLowerCase() || '';
    if (nombre.includes('nazca') || nombre.includes('líneas')) return '/images/nazcaLineas.png';
    if (nombre.includes('ballestas') || nombre.includes('paracas')) return '/images/islas-ballestas.png';
    if (nombre.includes('huacachina')) return '/images/huacachina.png';
    return '/images/placeholder.jpg';
  }
}
