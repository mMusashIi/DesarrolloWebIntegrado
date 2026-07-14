import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { NATIONAL_PHONE_PATTERN, PHONE_COUNTRIES, splitE164, toE164 } from '../../../shared/data/phone-countries';

@Component({
  selector: 'app-profile-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-modal.component.html'
})
export class ProfileModalComponent {
  @Output() close = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  countries = PHONE_COUNTRIES;
  loading = signal(false);
  error = signal('');

  private phone = splitE164(this.auth.currentUser()?.telefono);
  form = this.fb.group({
    nombre: [this.auth.currentUser()?.nombre || '', Validators.required],
    apellido: [this.auth.currentUser()?.apellido || '', Validators.required],
    prefijoTelefono: [this.phone.dialCode, Validators.required],
    telefono: [this.phone.nationalNumber, [Validators.required, Validators.pattern(NATIONAL_PHONE_PATTERN)]],
    nacionalidad: [this.auth.currentUser()?.nacionalidad || 'Perú', Validators.required],
    dni: [this.auth.currentUser()?.dni || '']
  });

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const value = this.form.getRawValue();
    this.loading.set(true);
    this.error.set('');
    this.auth.updateProfile({
      nombre: value.nombre!,
      apellido: value.apellido!,
      telefono: toE164(value.prefijoTelefono!, value.telefono!),
      nacionalidad: value.nacionalidad!,
      dni: value.dni || ''
    }).subscribe({
      next: () => { this.loading.set(false); this.close.emit(); },
      error: err => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'No se pudo actualizar el perfil.');
      }
    });
  }
}
