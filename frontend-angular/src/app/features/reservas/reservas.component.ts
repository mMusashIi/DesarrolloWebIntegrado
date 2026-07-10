import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { InventarioService } from '../../core/services/inventario.service';
import { ReservasService } from '../../core/services/reservas.service';
import { ApisNetService } from '../../core/services/apis-net.service';
import { AuthService } from '../../core/auth/auth.service';
import { Inventario } from '../../shared/models/inventario.model';
import { Reserva } from '../../shared/models/reserva.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './reservas.component.html'
})
export class ReservasComponent implements OnInit {
  private fb = inject(FormBuilder);
  private inventarioService = inject(InventarioService);
  private reservasService = inject(ReservasService);
  private apisNetService = inject(ApisNetService);
  auth = inject(AuthService);

  inventarios = signal<Inventario[]>([]);
  loading = signal(false);
  buscandoDni = signal(false);
  submitting = signal(false);
  error = signal('');
  successMsg = signal('');
  reservaCreada = signal<Reserva | null>(null);
  initPoint = signal('');

  form: FormGroup = this.fb.group({
    nombreCompleto: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    dni: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
    telefono: ['', Validators.required],
    idInventario: ['', Validators.required],
    cantidadPersonas: [1, [Validators.required, Validators.min(1)]],
    terminos: [false, Validators.requiredTrue]
  });

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (user) {
      this.form.patchValue({
        nombreCompleto: `${user.nombre} ${user.apellido}`,
        email: user.email,
        telefono: user.telefono || '',
        dni: user.dni || ''
      });
    }
    this.inventarioService.getDisponible().subscribe({
      next: (data) => this.inventarios.set(data)
    });
  }

  buscarDni(): void {
    const dni = this.form.get('dni')?.value;
    if (!dni || dni.length !== 8) return;
    this.buscandoDni.set(true);
    this.apisNetService.consultarDni(dni).subscribe({
      next: (data) => {
        this.form.patchValue({
          nombreCompleto: `${data.nombre} ${data.apellidoPaterno} ${data.apellidoMaterno}`
        });
        this.buscandoDni.set(false);
      },
      error: () => this.buscandoDni.set(false)
    });
  }

  get precioTotal(): number {
    const inv = this.inventarios().find(i => i.idInventario === +this.form.get('idInventario')!.value);
    if (!inv) return 0;
    return 0; // el precio viene del paquete, no del inventario directamente
  }

  getCuposDisponibles(): number {
    const id = +this.form.get('idInventario')!.value;
    const inv = this.inventarios().find(i => i.idInventario === id);
    return inv?.cupoDisponible || 0;
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const cantidadPersonas = +this.form.get('cantidadPersonas')!.value;
    if (cantidadPersonas > this.getCuposDisponibles()) {
      this.error.set(`Solo hay ${this.getCuposDisponibles()} cupos disponibles.`);
      return;
    }
    this.submitting.set(true);
    this.error.set('');
    const request = {
      idInventario: +this.form.get('idInventario')!.value,
      cantidadPersonas
    };
    this.reservasService.create(request).subscribe({
      next: (reserva) => {
        this.reservaCreada.set(reserva);
        this.reservasService.crearPreferenciaMP(reserva.idReserva).subscribe({
          next: (mp) => {
            this.initPoint.set(mp.initPoint);
            this.submitting.set(false);
          },
          error: () => {
            this.successMsg.set('Reserva creada. Podrás pagar luego desde tu perfil.');
            this.submitting.set(false);
          }
        });
      },
      error: (err) => {
        this.submitting.set(false);
        this.error.set(err?.error?.message || 'Error al crear la reserva. Intenta de nuevo.');
      }
    });
  }

  field(name: string) { return this.form.get(name)!; }

  getInventarioById(id: number): Inventario | undefined {
    return this.inventarios().find(i => i.idInventario === id);
  }
}
