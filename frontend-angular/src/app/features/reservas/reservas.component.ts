import { Component, OnInit, OnDestroy, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { InventarioService } from '../../core/services/inventario.service';
import { ReservasService } from '../../core/services/reservas.service';
import { ApisNetService } from '../../core/services/apis-net.service';
import { PaquetesService } from '../../core/services/paquetes.service';
import { AuthService } from '../../core/auth/auth.service';
import { Inventario } from '../../shared/models/inventario.model';
import { Paquete } from '../../shared/models/paquete.model';
import { Reserva } from '../../shared/models/reserva.model';
import { LoadingSpinnerComponent } from '../../shared/components/loading-spinner/loading-spinner.component';
import { NATIONAL_PHONE_PATTERN, PHONE_COUNTRIES, splitE164, toE164 } from '../../shared/data/phone-countries';

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './reservas.component.html'
})
export class ReservasComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private inventarioService = inject(InventarioService);
  private reservasService = inject(ReservasService);
  private apisNetService = inject(ApisNetService);
  private paquetesService = inject(PaquetesService);
  auth = inject(AuthService);
  countries = PHONE_COUNTRIES;

  inventarios = signal<Inventario[]>([]);
  paquetes = signal<Paquete[]>([]);
  loading = signal(false);
  buscandoDni = signal(false);
  submitting = signal(false);
  error = signal('');
  successMsg = signal('');
  reservaCreada = signal<Reserva | null>(null);
  initPoint = signal('');
  misReservas = signal<Reserva[]>([]);
  loadingMisReservas = signal(true);
  filtroCompras = signal<'todas' | 'pendiente' | 'confirmada' | 'cancelada'>('todas');
  cancelandoId = signal<number | null>(null);
  pagandoId = signal<number | null>(null);

  /** Estado del popup de MercadoPago */
  pagoPopupAbierto = signal(false);
  pagoResultado = signal<'success' | 'failure' | 'cancelled' | null>(null);
  pagoReservaId = signal<number | null>(null);

  private mpPopup: Window | null = null;
  private popupInterval: ReturnType<typeof setInterval> | null = null;

  comprasFiltradas = computed(() => {
    const filtro = this.filtroCompras();
    return filtro === 'todas' ? this.misReservas() : this.misReservas().filter(r => r.estado === filtro);
  });

  form: FormGroup = this.fb.group({
    nombreCompleto: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    dni: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9-]{5,20}$/)]],
    prefijoTelefono: ['+51', Validators.required],
    telefono: ['', [Validators.required, Validators.pattern(NATIONAL_PHONE_PATTERN)]],
    idInventario: ['', Validators.required],
    cantidadPersonas: [1, [Validators.required, Validators.min(1)]],
    terminos: [false, Validators.requiredTrue],
    whatsappOptIn: [true],
    viajerosAdicionales: this.fb.array([])
  });

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (user) {
      const phone = splitE164(user.telefono);
      this.form.patchValue({
        nombreCompleto: `${user.nombre} ${user.apellido}`,
        email: user.email,
        prefijoTelefono: phone.dialCode,
        telefono: phone.nationalNumber,
        dni: user.dni || ''
      });
    }
    this.inventarioService.getDisponible().subscribe({
      next: (data) => this.inventarios.set(data)
    });
    this.paquetesService.getActivos().subscribe({
      next: (data) => this.paquetes.set(data)
    });
    this.loadMisReservas();
    this.form.get('cantidadPersonas')?.valueChanges.subscribe(value => this.syncViajeros(+value || 1));
  }

  ngOnDestroy(): void {
    this.detenerPolling();
  }

  get viajerosAdicionales(): FormArray {
    return this.form.get('viajerosAdicionales') as FormArray;
  }

  private syncViajeros(cantidad: number): void {
    const adicionales = Math.max(0, cantidad - 1);
    while (this.viajerosAdicionales.length < adicionales) {
      this.viajerosAdicionales.push(this.fb.group({
        nombreCompleto: ['', Validators.required],
        prefijoTelefono: ['+51', Validators.required],
        telefono: ['', [Validators.required, Validators.pattern(NATIONAL_PHONE_PATTERN)]]
      }));
    }
    while (this.viajerosAdicionales.length > adicionales) {
      this.viajerosAdicionales.removeAt(this.viajerosAdicionales.length - 1);
    }
  }

  loadMisReservas(): void {
    this.loadingMisReservas.set(true);
    this.reservasService.getMisReservas().subscribe({
      next: data => {
        this.misReservas.set([...data].sort((a, b) => b.idReserva - a.idReserva));
        this.loadingMisReservas.set(false);
      },
      error: () => this.loadingMisReservas.set(false)
    });
  }

  buscarDni(): void {
    const dni = this.form.get('dni')?.value;
    if (!dni || dni.length !== 8) return;
    this.buscandoDni.set(true);
    this.apisNetService.consultarDni(dni).subscribe({
      next: (data) => {
        this.form.patchValue({
          nombreCompleto: [data.nombres, data.apellidoPaterno, data.apellidoMaterno]
            .filter(Boolean)
            .join(' ')
        });
        this.buscandoDni.set(false);
      },
      error: (err) => {
        this.buscandoDni.set(false);
        this.error.set(err?.error?.message || 'No se pudo consultar el DNI. Verifica el número e intenta nuevamente.');
      }
    });
  }

  get precioTotal(): number {
    const inv = this.inventarios().find(i => i.idInventario === +this.form.get('idInventario')!.value);
    if (!inv) return 0;
    const paquete = this.paquetes().find(p => p.idPaquete === inv.idPaquete);
    return (paquete?.precioBase || 0) * (+this.form.get('cantidadPersonas')!.value || 1);
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
    const inv = this.getInventarioById(+this.form.get('idInventario')!.value);
    const paquete = inv ? this.paquetes().find(p => p.idPaquete === inv.idPaquete) : undefined;
    const nombreCliente = String(this.form.get('nombreCompleto')!.value).trim();
    const telefonoCliente = toE164(this.form.get('prefijoTelefono')!.value, this.form.get('telefono')!.value);
    const viajerosAdicionales = this.viajerosAdicionales.getRawValue();
    const viajeros = [nombreCliente, ...viajerosAdicionales.map((v: any) => String(v.nombreCompleto).trim())];
    const telefonos = [telefonoCliente, ...viajerosAdicionales.map((v: any) => toE164(v.prefijoTelefono, v.telefono))];
    const user = this.auth.currentUser();
    const buyerPhone = splitE164(user?.telefono);
    const telefonoComprador = user?.telefono?.match(/^\+[1-9]\d{7,14}$/)
      ? user.telefono
      : toE164(buyerPhone.dialCode, buyerPhone.nationalNumber || this.form.get('telefono')!.value);
    const request = {
      idInventario: +this.form.get('idInventario')!.value,
      cantidadPersonas,
      nombreComprador: user ? `${user.nombre} ${user.apellido}`.trim() : nombreCliente,
      telefonoComprador,
      nombreCliente,
      nombresViajeros: viajeros.join(' | '),
      telefonosViajeros: telefonos.join(' | '),
      dniCliente: this.form.get('dni')!.value,
      emailCliente: this.form.get('email')!.value,
      telefonoCliente,
      whatsappOptIn: Boolean(this.form.get('whatsappOptIn')!.value),
      nombrePaquete: paquete?.nombrePaquete || inv?.nombrePaquete || 'Tour',
      fechaViaje: inv?.fechaSalida || ''
    };
    this.reservasService.create(request).subscribe({
      next: (reserva) => {
        this.reservaCreada.set(reserva);
        this.loadMisReservas();
        const descripcion = paquete?.nombrePaquete || inv?.nombrePaquete || 'Reserva Buganvilla Tours';
        this.reservasService.crearPreferenciaMP(reserva.idReserva, this.precioTotal, descripcion, cantidadPersonas).subscribe({
          next: (mp) => {
            this.submitting.set(false);
            this.abrirMercadoPago(mp.initPoint, reserva.idReserva);
          },
          error: () => {
            this.successMsg.set('Reserva creada. Podrás pagar luego desde Mis compras.');
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

  cancelarCompra(id: number): void {
    if (!confirm('¿Cancelar esta compra pendiente? Se restaurarán los cupos.')) return;
    this.cancelandoId.set(id);
    this.reservasService.cancelar(id).subscribe({
      next: () => { this.cancelandoId.set(null); this.loadMisReservas(); },
      error: err => {
        this.cancelandoId.set(null);
        this.error.set(err?.error?.message || 'No se pudo cancelar la compra.');
      }
    });
  }

  pagarReserva(r: Reserva): void {
    const inv = this.inventarios().find(i => i.idInventario === r.idInventario);
    if (inv) {
      this.procesarPagoConInventario(r, inv);
    } else {
      this.pagandoId.set(r.idReserva);
      this.inventarioService.getContextoPago(r.idInventario).subscribe({
        next: (inventario) => this.procesarPagoConInventario(r, inventario),
        error: () => {
          this.pagandoId.set(null);
          this.error.set('No se pudo cargar la información de la reserva para calcular el precio.');
        }
      });
    }
  }

  private procesarPagoConInventario(r: Reserva, inv: Inventario): void {
    this.pagandoId.set(r.idReserva);
    const paquete = this.paquetes().find(p => p.idPaquete === inv.idPaquete);
    if (paquete) {
      this.generarPreferencia(r, paquete.precioBase);
    } else {
      this.paquetesService.getById(inv.idPaquete).subscribe({
        next: (p) => this.generarPreferencia(r, p.precioBase),
        error: () => {
          this.pagandoId.set(null);
          this.error.set('No se pudo cargar el paquete para calcular el precio.');
        }
      });
    }
  }

  private generarPreferencia(r: Reserva, precioBase: number): void {
    const precioTotal = precioBase * r.cantidadPersonas;
    if (precioTotal === 0) {
      this.pagandoId.set(null);
      this.error.set('El precio calculado es 0. Contacta a soporte.');
      return;
    }

    const descripcion = r.nombrePaquete || 'Reserva Buganvilla Tours';
    this.reservasService.crearPreferenciaMP(r.idReserva, precioTotal, descripcion, r.cantidadPersonas).subscribe({
      next: (mp) => {
        this.pagandoId.set(null);
        this.abrirMercadoPago(mp.initPoint, r.idReserva);
      },
      error: () => {
        this.pagandoId.set(null);
        this.error.set('No se pudo generar el enlace de pago.');
      }
    });
  }

  /** Abre el checkout de MercadoPago en popup y monitorea su cierre */
  abrirMercadoPago(url: string, reservaId: number): void {
    this.detenerPolling();
    this.pagoResultado.set(null);
    this.pagoReservaId.set(reservaId);

    const ancho = Math.min(960, window.screen.width - 80);
    const alto  = Math.min(700, window.screen.height - 80);
    const left  = Math.round((window.screen.width  - ancho) / 2);
    const top   = Math.round((window.screen.height - alto)  / 2);
    const opts  = `width=${ancho},height=${alto},left=${left},top=${top},menubar=no,toolbar=no,location=no,status=no`;

    this.mpPopup = window.open(url, 'mp_checkout', opts);
    if (!this.mpPopup) {
      // Bloqueador de popups activo — fallback a nueva pestaña
      window.open(url, '_blank');
      return;
    }

    this.pagoPopupAbierto.set(true);

    // Polling: cada 3 s comprueba si la reserva cambió de estado
    this.popupInterval = setInterval(() => {
      const popupCerrado = !this.mpPopup || this.mpPopup.closed;

      this.reservasService.getById(reservaId).subscribe({
        next: (reserva) => {
          if (reserva.estado === 'confirmada') {
            this.cerrarPopupConResultado('success');
          } else if (reserva.estado === 'cancelada') {
            this.cerrarPopupConResultado('failure');
          } else if (popupCerrado) {
            // Popup cerrado sin que se confirmara el pago
            this.cerrarPopupConResultado('cancelled');
          }
        },
        error: () => {
          if (popupCerrado) this.cerrarPopupConResultado('cancelled');
        }
      });
    }, 3000);
  }

  private cerrarPopupConResultado(resultado: 'success' | 'failure' | 'cancelled'): void {
    this.detenerPolling();
    if (this.mpPopup && !this.mpPopup.closed) this.mpPopup.close();
    this.pagoPopupAbierto.set(false);
    this.pagoResultado.set(resultado);
    this.reservaCreada.set(null);
    this.loadMisReservas();
  }

  private detenerPolling(): void {
    if (this.popupInterval !== null) {
      clearInterval(this.popupInterval);
      this.popupInterval = null;
    }
  }

  cerrarResultado(): void {
    this.pagoResultado.set(null);
    this.pagoReservaId.set(null);
  }

  estadoLabel(estado: Reserva['estado']): string {
    return estado === 'confirmada' ? 'Realizada' : estado === 'cancelada' ? 'Cancelada' : 'Pendiente';
  }
}
