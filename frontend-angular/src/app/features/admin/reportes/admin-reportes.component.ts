import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportesService } from '../../../core/services/reportes.service';

@Component({
  selector: 'app-admin-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-reportes.component.html'
})
export class AdminReportesComponent {
  private reportesService = inject(ReportesService);

  downloadingExcel = signal(false);
  downloadingPdf = signal(false);
  error = signal('');

  descargarExcel(): void {
    this.downloadingExcel.set(true);
    this.error.set('');
    this.reportesService.getExcel().subscribe({
      next: (blob) => {
        this.reportesService.descargar(blob, 'reporte-reservas.xlsx');
        this.downloadingExcel.set(false);
      },
      error: (err) => {
        this.downloadingExcel.set(false);
        this.error.set(err?.error?.message || 'Error al descargar el reporte Excel.');
      }
    });
  }

  descargarPdf(): void {
    this.downloadingPdf.set(true);
    this.error.set('');
    this.reportesService.getPdf().subscribe({
      next: (blob) => {
        this.reportesService.descargar(blob, 'reporte-reservas.pdf');
        this.downloadingPdf.set(false);
      },
      error: (err) => {
        this.downloadingPdf.set(false);
        this.error.set(err?.error?.message || 'Error al descargar el reporte PDF.');
      }
    });
  }
}
