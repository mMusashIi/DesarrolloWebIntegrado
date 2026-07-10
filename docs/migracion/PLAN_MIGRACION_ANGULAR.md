# Plan de Migración Angular — Buganvilla Tours

**Fecha:** 2026-07-10  
**Versión Angular objetivo:** 18.x (LTS)  
**Directorio:** `frontend-angular/`

---

## Resumen Ejecutivo

La migración de React a Angular se realiza en paralelo al código React existente. El directorio `frontend/` (React) se mantiene intacto durante toda la migración. El directorio `frontend-angular/` (Angular) se crea como proyecto independiente. Solo después de confirmar paridad funcional y visual completa se procederá al reemplazo del frontend activo.

---

## Fase 3.0 — Scaffold del Proyecto Angular

### Comandos de inicialización
```bash
# Desde la raíz del repositorio
npx @angular/cli@18 new frontend-angular \
  --routing=true \
  --style=css \
  --strict=true \
  --standalone=false \
  --skip-git=true

cd frontend-angular

# Instalar Bootstrap 5
npm install bootstrap@5.3.x

# Instalar ESLint
ng add @angular-eslint/schematics

# Verificar que la app base compila
ng build
ng serve --port 4200
```

### Estructura inicial a crear manualmente
```
src/app/
├── core/
│   ├── auth/auth.service.ts
│   ├── auth/jwt.service.ts
│   ├── interceptors/auth.interceptor.ts
│   ├── interceptors/error.interceptor.ts
│   └── guards/auth.guard.ts / admin.guard.ts
├── shared/
│   ├── components/loading-spinner/
│   ├── components/header/
│   ├── components/footer/
│   └── models/ (interfaces TypeScript)
└── features/ (vacíos inicialmente)
```

---

## Fase 3.1 — Modelos TypeScript

Crear interfaces tipadas en `shared/models/`:

```typescript
// usuario.model.ts
export interface Usuario {
  idUsuario: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono?: string;
  nacionalidad?: string;
  dni?: string;
  rol: 'admin' | 'cliente';
  activo: boolean;
}

// paquete.model.ts
export interface Paquete {
  idPaquete: number;
  nombrePaquete: string;
  descripcion: string;
  precioBase: number;
  duracionDias: number;
  estado: 'activo' | 'inactivo';
  lugar: Lugar;
}

// inventario.model.ts
export interface Inventario {
  idInventario: number;
  idPaquete: number;
  nombrePaquete: string;
  fechaSalida: string;
  fechaRetorno?: string;
  cupoTotal: number;
  cupoDisponible: number;
}

// reserva.model.ts
export interface Reserva {
  idReserva: number;
  idUsuario: number;
  nombreCliente: string;
  idInventario: number;
  nombrePaquete: string;
  fechaViaje: string;
  cantidadPersonas: number;
  estado: 'pendiente' | 'confirmada' | 'cancelada';
  fechaReserva: string;
}

// pago.model.ts
export interface Pago {
  idPago: number;
  idReserva: number;
  monto: number;
  metodo: string;
  estado: 'pendiente' | 'completado' | 'rechazado' | 'en_proceso';
  mpStatus?: string;
}
```

---

## Fase 3.2 — Core: AuthService + JwtService + Interceptors

### AuthService
- `login(email, password)` → POST `/auth/login` → guarda sesión
- `logout()` → limpia localStorage → navega a `/`
- `register(userData)` → POST `/auth/register` → guarda sesión
- `getProfile()` → GET `/auth/profile`
- `currentUser$` — Signal o BehaviorSubject con el usuario actual
- `isAuthenticated` — getter boolean
- `isAdmin` — getter boolean

### JwtService
- `getToken()`, `setToken()`, `removeToken()`
- `getUser()`, `setUser()`, `removeUser()`
- `clearSession()`

### AuthInterceptor
```typescript
intercept(req: HttpRequest<unknown>, next: HttpHandler) {
  const token = this.jwt.getToken();
  if (token) {
    req = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
  }
  return next.handle(req);
}
```

### ErrorInterceptor
```typescript
intercept(req: HttpRequest<unknown>, next: HttpHandler) {
  return next.handle(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        this.auth.logout();
        this.router.navigate(['/']);
      }
      return throwError(() => error);
    })
  );
}
```

---

## Fase 3.3 — Guards

### AuthGuard
```typescript
canActivate(): boolean | UrlTree {
  if (this.auth.isAuthenticated) return true;
  return this.router.createUrlTree(['/']);
}
```

### AdminGuard
```typescript
canActivate(): boolean | UrlTree {
  if (this.auth.isAdmin) return true;
  return this.router.createUrlTree(['/']);
}
```

---

## Fase 3.4 — Shared Components

- `HeaderComponent` — Navbar con menú, dropdown usuario
- `FooterComponent` — Footer con links
- `LoadingSpinnerComponent` — Spinner reutilizable
- `AlertComponent` — Mensajes de éxito/error

---

## Fase 3.5 — Feature: Home

Componentes en `features/home/`:
- `HomeComponent` (página)
- `HeroSectionComponent`
- `StatsSectionComponent`
- `FeaturedPackagesComponent` (consume `PaquetesService.getActivos()`)
- `TestimonialsComponent`
- `MapSectionComponent`

---

## Fase 3.6 — Feature: Paquetes

- `PaquetesComponent` (página, consume `PaquetesService` + `InventarioService`)
- `PaqueteGridComponent`
- `PaqueteModalComponent` (con selector de inventario y botón de reservar)
- `PaqueteFiltrosComponent`

---

## Fase 3.7 — Feature: Autenticación

- `LoginModalComponent`
- `RegisterModalComponent` (con fix del bug de auto-login)
- `AuthRequiredModalComponent`
- `RegisterComponent` (página `/register`)

---

## Fase 3.8 — Feature: Reservas

- `ReservasComponent` (página, guard: AuthGuard)
- `ReservaFormComponent` (Reactive Form con validaciones)
  - Consulta DNI via `ApisNetService`
  - Selector de inventario disponible
  - Validación de cantidad vs cupo
- `ReservaSummaryComponent`
- `ConfirmacionModalComponent` → inicia flujo MercadoPago

---

## Fase 3.9 — Feature: Admin (La más importante)

Todos con guard: AdminGuard

- `AdminComponent` — layout con sidebar y router-outlet
- `AdminSidebarComponent`
- `DashboardComponent` — **con datos REALES** (no mock)
- `PaquetesAdminComponent` — CRUD completo
- `PaqueteFormModalComponent`
- `InventarioAdminComponent`
- `ReservasAdminComponent` — **con datos REALES** (no mock)
- `ReportesAdminComponent` — descargas Excel/PDF
- `UsuariosAdminComponent` (nuevo)
- `LugaresAdminComponent` (nuevo)

---

## Fase 3.10 — Feature: Nosotros y Contacto

- `NosotrosComponent`
- `ContactoComponent` (form documentado como pendiente de backend)

---

## Fase 3.11 — Servicios HTTP

Cada servicio extiende de un base genérico o inyecta `HttpClient` directamente:

```typescript
// paquetes.service.ts
@Injectable({ providedIn: 'root' })
export class PaquetesService {
  private baseUrl = `${environment.apiUrl}/paquetes`;
  
  getActivos(): Observable<Paquete[]> { return this.http.get<Paquete[]>(`${this.baseUrl}/activos`); }
  getById(id: number): Observable<Paquete> { return this.http.get<Paquete>(`${this.baseUrl}/${id}`); }
  search(params: PaqueteSearchParams): Observable<Paquete[]> { return this.http.get<Paquete[]>(`${this.baseUrl}/public/search`, { params }); }
  create(paquete: Partial<Paquete>): Observable<Paquete> { return this.http.post<Paquete>(this.baseUrl, paquete); }
  update(id: number, paquete: Partial<Paquete>): Observable<Paquete> { return this.http.put<Paquete>(`${this.baseUrl}/${id}`, paquete); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
}
```

---

## Fase 3.12 — Docker Angular

```dockerfile
# frontend-angular/Dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/frontend-angular/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

```nginx
# frontend-angular/nginx.conf
server {
    listen 80;
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
}
```

---

## Criterio de Completitud

La Fase 3 está completa cuando:
1. `ng build` sin errores
2. Todos los componentes de la `MATRIZ_PARIDAD_REACT_ANGULAR.md` en estado "Completada"
3. Tests unitarios pasan (`ng test --watch=false`)
4. Navegación manual confirma paridad visual con React

---

## Orden de Implementación (Priorizado)

```
Semana 1:
  3.0 Scaffold → 3.1 Modelos → 3.2 Core (Auth + Interceptors) → 3.3 Guards
  → 3.4 Shared → 3.5 Home

Semana 2:
  3.6 Paquetes → 3.7 Auth modals → 3.8 Reservas

Semana 3:
  3.9 Admin completo (dashboard real, reservas reales)

Semana 4:
  3.10 Nosotros/Contacto → 3.11 Verificar servicios → 3.12 Docker Angular
  → Tests → Paridad visual
```
