# Decisiones de Arquitectura — Migración Angular

**Fecha:** 2026-07-10

---

## 1. Versión Angular: 18

**Decisión:** Angular 18.x

**Justificación:**
- Compatible con Node.js 18, 20 y 22 (el sistema tiene Node 25.9.0 — compatible)
- Soporta componentes standalone (sin necesidad de declarar en módulos)
- Incluye Signals como API de reactividad opcional (complementa RxJS)
- Es la versión estable más reciente con LTS garantizado
- No requiere actualizar Node.js (regla del plan: no actualizar Node sin necesidad)

**Alternativa descartada:** Angular 17 — mismo soporte pero sin Signal effects estables.

---

## 2. Estrategia de Componentes: Standalone

**Decisión:** Usar componentes standalone (`standalone: true`) como patrón principal.

**Justificación:**
- Angular 18 promueve standalone como patrón moderno
- Evita la complejidad de NgModules para features independientes
- El AppModule principal sigue siendo necesario para configurar providers globales (Router, HttpClient)
- Lazy loading funciona bien con standalone components

**Patrón aplicado:**
```typescript
@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  selector: 'app-paquete-grid',
  templateUrl: './paquete-grid.component.html'
})
export class PaqueteGridComponent { ... }
```

---

## 3. Reactividad: RxJS + Signals (Híbrido)

**Decisión:** RxJS para comunicación HTTP y datos asíncronos; Signals para estado local de componentes.

**Justificación:**
- `HttpClient` de Angular ya retorna `Observable` (RxJS) — no hay alternativa directa
- Signals son ideales para estado de UI local (loading, error, selected item)
- La combinación es la recomendación oficial de Angular 18
- Evita refactorizar todo a una solución de estado global prematura

**Patrón:**
```typescript
// En servicios — RxJS
getPackages(): Observable<Paquete[]> {
  return this.http.get<Paquete[]>('/api/paquetes/activos');
}

// En componentes — Signals para UI local
loading = signal(false);
packages = signal<Paquete[]>([]);

ngOnInit() {
  this.loading.set(true);
  this.packageService.getPackages().subscribe({
    next: (data) => { this.packages.set(data); this.loading.set(false); },
    error: () => this.loading.set(false)
  });
}
```

---

## 4. Formularios: Reactive Forms

**Decisión:** Reactive Forms (`FormGroup`, `FormControl`, `Validators`)

**Justificación:**
- El formulario de reserva tiene validaciones complejas (campos dependientes, validación de DNI asíncrona)
- Reactive Forms son más testables (se pueden probar sin renderizar el DOM)
- Template-driven forms son más simples pero menos controlables para forms complejos
- Angular 18 mejora el tipado de Reactive Forms (typed forms)

---

## 5. Estructura de Directorios

```
frontend-angular/src/app/
├── core/
│   ├── auth/
│   │   ├── auth.service.ts           Lógica de autenticación (login, logout, register)
│   │   └── jwt.service.ts            Manejo seguro del token (get, set, remove)
│   ├── interceptors/
│   │   ├── auth.interceptor.ts       Agrega header Authorization a todas las requests
│   │   └── error.interceptor.ts      Maneja 401 (logout) y errores globales
│   ├── guards/
│   │   ├── auth.guard.ts             Redirige si no está autenticado
│   │   └── admin.guard.ts            Redirige si no es admin
│   └── models/
│       (mismo que shared/models/)
│
├── shared/
│   ├── components/
│   │   ├── loading-spinner/          Spinner de carga reutilizable
│   │   ├── modal/                    Modal base reutilizable
│   │   ├── alert/                    Componente de alertas/mensajes
│   │   └── header/ footer/           Navbar y footer globales
│   └── models/
│       ├── usuario.model.ts
│       ├── paquete.model.ts
│       ├── inventario.model.ts
│       ├── reserva.model.ts
│       ├── pago.model.ts
│       └── lugar.model.ts
│
└── features/
    ├── auth/                         Login modal, Register modal, AuthRequired modal
    ├── home/                         Hero, Stats, FeaturedPackages, Testimonials, Map
    ├── paquetes/                     PackageGrid, PackageModal, PackageFilters
    ├── reservas/                     ReservationForm, ReservationSummary, Confirmation
    ├── pagos/                        MercadoPago redirect handlers
    ├── perfil/                       User profile view/edit
    ├── nosotros/                     About, Contact
    └── admin/
        ├── admin.component.ts        Layout con sidebar
        ├── dashboard/                Dashboard con datos reales (no mock)
        ├── paquetes/                 CRUD paquetes
        ├── inventario/               Gestión de cupos
        ├── reservas/                 Gestión de reservas (datos reales)
        ├── usuarios/                 Gestión de usuarios
        ├── lugares/                  Gestión de lugares
        └── reportes/                 Descarga Excel/PDF
```

---

## 6. Routing

```typescript
// app.routes.ts
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'paquetes', component: PaquetesComponent },
  { path: 'reservas', component: ReservasComponent, canActivate: [AuthGuard] },
  { path: 'nosotros', component: NosotrosComponent },
  { path: 'contacto', component: ContactoComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard, AdminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/admin/dashboard/...')},
      { path: 'paquetes', loadComponent: () => import('./features/admin/paquetes/...') },
      { path: 'inventario', loadComponent: () => import('./features/admin/inventario/...')},
      { path: 'reservas', loadComponent: () => import('./features/admin/reservas/...')},
      { path: 'usuarios', loadComponent: () => import('./features/admin/usuarios/...')},
      { path: 'lugares', loadComponent: () => import('./features/admin/lugares/...')},
      { path: 'reportes', loadComponent: () => import('./features/admin/reportes/...')},
    ]
  },
  { path: '**', redirectTo: '' }
];
```

**Lazy loading** en sub-rutas del admin para optimizar el bundle inicial.

---

## 7. Variables de Entorno

```typescript
// src/environments/environment.ts (desarrollo)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// src/environments/environment.prod.ts (producción)
export const environment = {
  production: true,
  apiUrl: '/api'  // Nginx proxy o URL absoluta
};
```

---

## 8. Manejo de Token JWT

**Almacenamiento:** `localStorage` (mismo que React, para compatibilidad)
- Claves: `buganvilla_token`, `buganvilla_user`

**Justificación de mantener localStorage:**
- Compatibilidad con el comportamiento existente
- Para una SPA pública sin requisitos de seguridad ultra-alta, es aceptable
- `httpOnly cookies` requieren cambios en el backend (fuera de scope)

**Implementación en `JwtService`:**
```typescript
@Injectable({ providedIn: 'root' })
export class JwtService {
  private readonly TOKEN_KEY = 'buganvilla_token';
  private readonly USER_KEY = 'buganvilla_user';

  getToken(): string | null { return localStorage.getItem(this.TOKEN_KEY); }
  getUser(): Usuario | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }
  setSession(token: string, user: Usuario): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }
  clearSession(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }
}
```

---

## 9. Diseño Visual

**Principio:** Paridad primero, mejoras después.

La migración Angular reproducirá exactamente:
- Paleta de colores: violeta `#4c1d95`, rosado `#db2777`
- Bootstrap 5.3.x (misma versión principal)
- Tipografía y espaciados actuales
- Estructura de layouts (header + contenido + footer)

**Las mejoras visuales** (si se proponen) se documentarán en un archivo separado `MEJORAS_VISUALES_PROPUESTAS.md` y se implementarán solo después de confirmar paridad funcional.

---

## 10. Testing

**Framework:** Jasmine + Karma (incluido con Angular CLI)
**E2E:** Playwright

**Cobertura mínima requerida:**
- Services: login, logout, register, getPackages, createReservation
- Interceptors: agrega header, maneja 401
- Guards: AuthGuard, AdminGuard
- Componentes críticos: ReservationForm (validaciones), PackageGrid (renderizado)
