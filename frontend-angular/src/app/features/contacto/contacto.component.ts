import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { inject } from '@angular/core';

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './contacto.component.html'
})
export class ContactoComponent {
  private fb = inject(FormBuilder);

  enviado = signal(false);
  enviando = signal(false);

  form: FormGroup = this.fb.group({
    nombre: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    asunto: ['', Validators.required],
    mensaje: ['', [Validators.required, Validators.minLength(20)]]
  });

  field(name: string) { return this.form.get(name)!; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.enviando.set(true);
    setTimeout(() => {
      this.enviado.set(true);
      this.enviando.set(false);
    }, 1000);
  }
}
