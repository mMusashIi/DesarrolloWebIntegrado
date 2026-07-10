import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuariosService } from '../../../core/services/usuarios.service';
import { Usuario } from '../../../shared/models/usuario.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  templateUrl: './admin-usuarios.component.html'
})
export class AdminUsuariosComponent implements OnInit {
  private usuariosService = inject(UsuariosService);

  usuarios = signal<Usuario[]>([]);
  loading = signal(true);
  deleting = signal<number | null>(null);

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.usuariosService.getAll().subscribe({
      next: (data) => { this.usuarios.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  delete(id: number): void {
    if (!confirm('¿Eliminar este usuario? Esta acción no se puede deshacer.')) return;
    this.deleting.set(id);
    this.usuariosService.delete(id).subscribe({
      next: () => { this.deleting.set(null); this.load(); },
      error: (err) => {
        this.deleting.set(null);
        alert(err?.error?.message || 'Error al eliminar.');
      }
    });
  }
}
