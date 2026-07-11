import { Component, EventEmitter, Output, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { NATIONAL_PHONE_PATTERN, PHONE_COUNTRIES, toE164 } from '../../../shared/data/phone-countries';

@Component({
  selector: 'app-register-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-modal.component.html'
})
export class RegisterModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() success = new EventEmitter<void>();

  private fb = inject(FormBuilder);
  auth = inject(AuthService);
  countries = PHONE_COUNTRIES;

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    apellido: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    prefijoTelefono: ['+51', Validators.required],
    telefono: ['', [Validators.required, Validators.pattern(NATIONAL_PHONE_PATTERN)]],
    nacionalidad: ['Perú', Validators.required]
  });

  loading = signal(false);
  error = signal('');

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading.set(true);
    this.error.set('');
    const value = this.form.value;
    const request = {
      nombre: value.nombre,
      apellido: value.apellido,
      email: value.email,
      password: value.password,
      telefono: toE164(value.prefijoTelefono, value.telefono),
      nacionalidad: value.nacionalidad
    };
    this.auth.register(request).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.emit();
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Error al registrarse. Verifica los datos.');
      }
    });
  }

  field(name: string) { return this.form.get(name)!; }
}
