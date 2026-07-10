import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { PaquetesService } from '../../../core/services/paquetes.service';
import { LugaresService } from '../../../core/services/lugares.service';
import { Paquete } from '../../../shared/models/paquete.model';
import { Lugar } from '../../../shared/models/lugar.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-admin-paquetes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './admin-paquetes.component.html'
})
export class AdminPaquetesComponent implements OnInit {
  private paquetesService = inject(PaquetesService);
  private lugaresService = inject(LugaresService);
  private fb = inject(FormBuilder);

  paquetes = signal<Paquete[]>([]);
  lugares = signal<Lugar[]>([]);
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  showForm = signal(false);
  editingId = signal<number | null>(null);

  form: FormGroup = this.fb.group({
    nombrePaquete: ['', Validators.required],
    descripcion: ['', Validators.required],
    precioBase: [0, [Validators.required, Validators.min(1)]],
    duracionDias: [1, [Validators.required, Validators.min(1)]],
    estado: ['activo', Validators.required],
    idLugar: ['', Validators.required]
  });

  ngOnInit(): void {
    this.load();
    this.lugaresService.getAll().subscribe({ next: (d) => this.lugares.set(d) });
  }

  load(): void {
    this.loading.set(true);
    this.paquetesService.getAll().subscribe({
      next: (data) => { this.paquetes.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset({ estado: 'activo', precioBase: 0, duracionDias: 1 });
    this.showForm.set(true);
  }

  openEdit(p: Paquete): void {
    this.editingId.set(p.idPaquete);
    this.form.patchValue({
      nombrePaquete: p.nombrePaquete,
      descripcion: p.descripcion,
      precioBase: p.precioBase,
      duracionDias: p.duracionDias,
      estado: p.estado,
      idLugar: p.lugar?.idLugar || ''
    });
    this.showForm.set(true);
  }

  closeForm(): void { this.showForm.set(false); this.error.set(''); }

  field(name: string) { return this.form.get(name)!; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);
    this.error.set('');
    const data = { ...this.form.value, idLugar: +this.form.value.idLugar };
    const id = this.editingId();
    const req = id
      ? this.paquetesService.update(id, data)
      : this.paquetesService.create(data);

    req.subscribe({
      next: () => { this.saving.set(false); this.closeForm(); this.load(); },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Error al guardar. Intenta de nuevo.');
      }
    });
  }

  delete(id: number): void {
    if (!confirm('¿Eliminar este paquete?')) return;
    this.paquetesService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => alert(err?.error?.message || 'Error al eliminar.')
    });
  }
}
