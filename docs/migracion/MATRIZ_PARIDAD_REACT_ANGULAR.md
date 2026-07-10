# Matriz de Paridad React → Angular

**Fecha:** 2026-07-10  
**Estado inicial:** Todos en "Pendiente" hasta completar Fase 3

---

## Páginas Principales

| Ruta React | Componente React | Endpoint(s) | Roles | Equivalente Angular | Estado |
|-----------|-----------------|------------|-------|-------------------|--------|
| `/` | `Home.jsx` | `/paquetes/activos`, `/inventario/disponible` | Público | `HomeComponent` | Pendiente |
| `/paquetes` | `Packages.jsx` | `/paquetes/activos`, `/inventario/disponible`, `/paquetes/public/search` | Público | `PaquetesComponent` | Pendiente |
| `/reservas` | `Reservations.jsx` | `/inventario/disponible`, `/reservas` (POST), `/apis-net/dni/{dni}`, `/mercadopago/crear-preferencia` | Autenticado | `ReservasComponent` | Pendiente |
| `/admin` | `Admin.jsx` | Múltiples admin endpoints | Admin | `AdminComponent` | Pendiente |
| `/nosotros` | `About.jsx` | Ninguno | Público | `NosotrosComponent` | Pendiente |
| `/contacto` | `Contact.jsx` | Ninguno (simulado) | Público | `ContactoComponent` | Pendiente |
| `/register` | `Register.jsx` | `/auth/register` | Público | `RegisterComponent` | Pendiente |

---

## Componentes Auth

| Componente React | Funcionalidad | Equivalente Angular | Estado |
|-----------------|---------------|-------------------|--------|
| `LoginModal.jsx` | Modal login, redirect por rol | `LoginModalComponent` | Pendiente |
| `RegisterModal.jsx` | Modal registro (con fix del bug de auto-login) | `RegisterModalComponent` | Pendiente |
| `AuthRequiredModal.jsx` | Modal cuando intenta reservar sin auth | `AuthRequiredModalComponent` | Pendiente |

---

## Componentes Home

| Componente React | Funcionalidad | Equivalente Angular | Estado |
|-----------------|---------------|-------------------|--------|
| `HeroSection.jsx` | Banner principal con CTA | `HeroSectionComponent` | Pendiente |
| `StatsSection.jsx` | Contadores animados | `StatsSectionComponent` | Pendiente |
| `FeaturedPackages.jsx` | Grid paquetes destacados | `FeaturedPackagesComponent` | Pendiente |
| `Testimonials.jsx` | Carrusel testimonios | `TestimonialsComponent` | Pendiente |
| `MapSection.jsx` | Sección de mapa | `MapSectionComponent` | Pendiente |

---

## Componentes Packages

| Componente React | Funcionalidad | Equivalente Angular | Estado |
|-----------------|---------------|-------------------|--------|
| `PackageGrid.jsx` | Grid de tarjetas de paquetes | `PaqueteGridComponent` | Pendiente |
| `PackageModal.jsx` | Modal detalle + reservar | `PaqueteModalComponent` | Pendiente |
| `PackageFilters.jsx` | Filtros búsqueda y categoría | `PaqueteFiltrosComponent` | Pendiente |

---

## Componentes Reservations

| Componente React | Funcionalidad | Equivalente Angular | Estado |
|-----------------|---------------|-------------------|--------|
| `ReservationForm.jsx` | Formulario reserva + DNI | `ReservaFormComponent` | Pendiente |
| `ReservationSummary.jsx` | Panel resumen lateral | `ReservaSummaryComponent` | Pendiente |
| `ConfirmationModal.jsx` | Modal confirmación + MP | `ConfirmacionModalComponent` | Pendiente |

---

## Componentes Admin

| Componente React | Funcionalidad | Equivalente Angular | Mejora vs React | Estado |
|-----------------|---------------|-------------------|-----------------|--------|
| `AdminSidebar.jsx` | Navegación lateral | `AdminSidebarComponent` | Sin cambios | Pendiente |
| `Dashboard.jsx` | Stats y actividad (MOCK → REAL) | `DashboardComponent` | **Conectar a API real** | Pendiente |
| `PackagesManagement.jsx` | CRUD paquetes | `PaquetesAdminComponent` | Sin cambios | Pendiente |
| `PackageFormModal.jsx` | Modal form paquete | `PaqueteFormModalComponent` | Sin cambios | Pendiente |
| `PackagesInventory.jsx` | Inventario cupos | `InventarioAdminComponent` | Sin cambios | Pendiente |
| `ReservationsManagement.jsx` | Gestión reservas (MOCK → REAL) | `ReservasAdminComponent` | **Conectar a /api/reservas** | Pendiente |
| `Reports.jsx` | Descargas Excel/PDF | `ReportesAdminComponent` | Stats reales | Pendiente |
| `ProductsInventory.jsx` | No implementado | `ProductosInventarioComponent` | Documentar como futuro | Pendiente |
| `Transports.jsx` | No implementado | `TransportesComponent` | Documentar como futuro | Pendiente |

**Nuevos componentes en Angular (no existen en React):**
- `UsuariosAdminComponent` — gestión de usuarios (CRUD vía `/api/usuarios`)
- `LugaresAdminComponent` — gestión de lugares (CRUD vía `/api/lugares`)

---

## Componentes Common

| Componente React | Funcionalidad | Equivalente Angular | Estado |
|-----------------|---------------|-------------------|--------|
| `Header.jsx` | Navbar global | `HeaderComponent` | Pendiente |
| `Footer.jsx` | Footer global | `FooterComponent` | Pendiente |
| `LoadingSpinner.jsx` | Spinner de carga | `LoadingSpinnerComponent` | Pendiente |

---

## Servicios HTTP

| Servicio React (`api.js`) | Equivalente Angular | Estado |
|--------------------------|-------------------|--------|
| `authAPI` | `AuthService` | Pendiente |
| `packagesAPI` | `PaquetesService` | Pendiente |
| `lugaresAPI` | `LugaresService` | Pendiente |
| `inventoryAPI` | `InventarioService` | Pendiente |
| `reservationsAPI` | `ReservasService` | Pendiente |
| `reportsAPI` | `ReportesService` | Pendiente |
| `apisNetAPI` | `ApisNetService` | Pendiente |
| `mercadoPagoAPI` | `MercadoPagoService` | Pendiente |

---

## Comportamientos de Seguridad

| Comportamiento React | Equivalente Angular | Estado |
|---------------------|-------------------|--------|
| Agrega `Authorization: Bearer` en cada request | `AuthInterceptor` | Pendiente |
| Redirige a `/` en 401 + limpia localStorage | `ErrorInterceptor` (maneja 401) | Pendiente |
| Muestra "Acceso Denegado" si no es admin | `AdminGuard` → redirect a `/` | Pendiente |
| Muestra `AuthRequiredModal` si no autenticado | `AuthGuard` → redirect a `/` o modal | Pendiente |

**Nota:** El comportamiento de "Acceso Denegado en la misma página" (React) cambiará a "redirect al home" (Angular con Guards). Esta es una mejora, no una regresión.

---

## Validaciones (ReservationForm)

| Validación React | Equivalente Angular (Reactive Forms) | Estado |
|-----------------|--------------------------------------|--------|
| Campo nombre requerido | `Validators.required` | Pendiente |
| Email con formato válido | `Validators.email` | Pendiente |
| DNI con 8 dígitos | `Validators.pattern(/^\d{8}$/)` | Pendiente |
| Teléfono requerido | `Validators.required` | Pendiente |
| Cantidad personas >= 1 | `Validators.min(1)` | Pendiente |
| Cantidad personas <= cupo disponible | Validador custom | Pendiente |
| Inventario seleccionado | `Validators.required` | Pendiente |
| Términos aceptados | `Validators.requiredTrue` | Pendiente |

---

## Estados Visuales a Reproducir

| Estado | Pantallas afectadas | Implementación Angular |
|--------|--------------------|-----------------------|
| Carga inicial | Home, Packages, Admin | Signal `loading` + `LoadingSpinnerComponent` |
| Lista vacía | Packages (sin resultados), Reservas | Template con `@if (packages().length === 0)` |
| Error de API | Todas con requests | `ErrorInterceptor` + alert component |
| Autenticación requerida | Reservas, Admin | Modal o redirect |
| Operación exitosa | Formularios | Alert verde temporal |
| Operación fallida | Formularios | Alert rojo con mensaje |

---

## Leyenda de Estado

| Estado | Descripción |
|--------|-------------|
| Pendiente | Aún no iniciada |
| En progreso | Siendo implementada |
| Completada | Implementada y probada |
| Verificada | Paridad visual y funcional confirmada |
