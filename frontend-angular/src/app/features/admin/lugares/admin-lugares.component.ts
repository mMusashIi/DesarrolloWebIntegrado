import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { LugaresService } from '../../../core/services/lugares.service';
import { Lugar } from '../../../shared/models/lugar.model';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-admin-lugares',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './admin-lugares.component.html'
})
export class AdminLugaresComponent implements OnInit {
  private lugaresService = inject(LugaresService);
  private fb = inject(FormBuilder);

  lugares = signal<Lugar[]>([]);
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  showForm = signal(false);
  editingId = signal<number | null>(null);

  form: FormGroup = this.fb.group({
    nombreLugar: ['', Validators.required],
    ciudad: ['', Validators.required],
    descripcion: ['']
  });

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.lugaresService.getAll().subscribe({
      next: (data) => { this.lugares.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset();
    this.showForm.set(true);
  }

  openEdit(l: Lugar): void {
    this.editingId.set(l.idLugar);
    this.form.patchValue(l);
    this.showForm.set(true);
  }

  closeForm(): void { this.showForm.set(false); this.error.set(''); }

  field(name: string) { return this.form.get(name)!; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);
    this.error.set('');
    const id = this.editingId();
    const req = id
      ? this.lugaresService.update(id, this.form.value)
      : this.lugaresService.create(this.form.value);

    req.subscribe({
      next: () => { this.saving.set(false); this.closeForm(); this.load(); },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err?.error?.message || 'Error al guardar.');
      }
    });
  }

  delete(id: number): void {
    if (!confirm('¿Eliminar este lugar?')) return;
    this.lugaresService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => alert(err?.error?.message || 'Error al eliminar.')
    });
  }
}
