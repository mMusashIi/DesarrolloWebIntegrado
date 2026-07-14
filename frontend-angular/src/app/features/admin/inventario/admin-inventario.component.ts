import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { InventarioService } from '../../../core/services/inventario.service';
import { PaquetesService } from '../../../core/services/paquetes.service';
import { Inventario } from '../../../shared/models/inventario.model';
import { Paquete } from '../../../shared/models/paquete.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-admin-inventario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './admin-inventario.component.html'
})
export class AdminInventarioComponent implements OnInit {
  private inventarioService = inject(InventarioService);
  private paquetesService = inject(PaquetesService);
  private fb = inject(FormBuilder);

  inventarios = signal<Inventario[]>([]);
  paquetes = signal<Paquete[]>([]);
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  showForm = signal(false);
  editingId = signal<number | null>(null);

  form: FormGroup = this.fb.group({
    idPaquete: ['', Validators.required],
    fechaSalida: ['', Validators.required],
    cupoTotal: [10, [Validators.required, Validators.min(1)]],
    cupoDisponible: [10, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.load();
    this.paquetesService.getAll().subscribe({ next: (d) => this.paquetes.set(d) });
  }

  load(): void {
    this.loading.set(true);
    this.inventarioService.getAll().subscribe({
      next: (data) => { this.inventarios.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset({ cupoTotal: 10, cupoDisponible: 10 });
    this.showForm.set(true);
  }

  openEdit(inv: Inventario): void {
    this.editingId.set(inv.idInventario);
    this.form.patchValue({
      idPaquete: inv.idPaquete,
      fechaSalida: inv.fechaSalida,
      cupoTotal: inv.cupoTotal,
      cupoDisponible: inv.cupoDisponible
    });
    this.showForm.set(true);
  }

  closeForm(): void { this.showForm.set(false); this.error.set(''); }

  field(name: string) { return this.form.get(name)!; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);
    this.error.set('');
    const data = { ...this.form.value, idPaquete: +this.form.value.idPaquete };
    const id = this.editingId();
    const req = id
      ? this.inventarioService.update(id, data)
      : this.inventarioService.create(data);

    req.subscribe({
      next: () => { this.saving.set(false); this.closeForm(); this.load(); },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Error al guardar.');
      }
    });
  }

  delete(id: number): void {
    if (!confirm('¿Eliminar esta fecha de salida?')) return;
    this.inventarioService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => alert(err?.error?.message || 'Error al eliminar.')
    });
  }

  getPaqueteName(idPaquete: number): string {
    return this.paquetes().find(p => p.idPaquete === idPaquete)?.nombrePaquete || '—';
  }
}
