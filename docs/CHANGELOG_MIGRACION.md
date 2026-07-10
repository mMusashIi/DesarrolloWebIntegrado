# Changelog — Modernización Buganvilla Tours

**Rama:** `refactor/angular-backend-whatsapp`  
**Fecha:** 2026-07-10  
**Base:** `feature/resynced` (commit `1bd8d52`)

---

## [FASE-1 a FASE-3] Angular 18 Frontend

### Añadido
- Proyecto Angular 18 en `frontend-angular/` con estructura feature-based
- Componentes standalone con lazy loading para todas las rutas
- `AuthService` con signals (`signal`, `computed`) para estado reactivo
- `JwtService` — persistencia en `localStorage` (claves `buganvilla_token`, `buganvilla_user`)
- `authInterceptor` — agrega `Authorization: Bearer` a todas las requests
- `errorInterceptor` — captura 401/403, hace logout automático en 401
- `authGuard` y `adminGuard` — guards funcionales (`CanActivateFn`)
- Rutas lazy-loaded: home, paquetes, reservas, nosotros, contacto, admin y sub-rutas
- Panel admin completo: dashboard, paquetes, inventario, reservas, usuarios, lugares, reportes
- Integración con todos los endpoints del backend (sin mocks en admin)
- Paleta de colores: violeta `#4c1d95`, rosado `#db2777` (idéntica a React)
- Descarga de reportes Excel y PDF desde el backend
- Consulta DNI via `/api/apis-net/dni/{dni}` en formulario de reserva
- Responsive mobile/tablet/desktop con Bootstrap 5

### Conservado
- `frontend/` (React 19) sin ninguna modificación

---

## [FASE-4] Mejoras Backend

### Corregido
- **Bug crítico:** `cancelarReserva()` ahora restaura los cupos al inventario antes de cancelar
  (antes se cancelaba la reserva sin devolver el cupo disponible)

### Añadido
- `GlobalExceptionHandler` (`@RestControllerAdvice`) con manejo consistente de excepciones
- CORS configurable via `CORS_ALLOWED_ORIGINS` (elimina el wildcard `*`)

### Cambiado
- Todos los secretos movidos a variables de entorno con fallbacks seguros
- `show-sql` desactivado por defecto en producción
- `application-dev.properties` usa env vars con defaults para desarrollo

---

## [FASE-5] WhatsApp Business Cloud API

### Añadido
- `WhatsAppNotificationProvider` — interface de abstracción
- `MockWhatsAppProvider` — activo por defecto, solo logs (sin envíos reales)
- `MetaWhatsAppCloudProvider` — skeleton para Meta Graph API (desactivado hasta credenciales)
- `WhatsAppNotificationService` — facade `@Async` con flag `whatsapp.enabled`

### Cambiado
- `MercadoPagoService`: reemplaza `OpenWAService.sendTextMessage()` + `new Thread()` 
  por `whatsAppNotificationService.notifyPaymentConfirmation()` (async, no bloquea webhook)

### Conservado
- `OpenWAService.java` — deprecado pero no eliminado (compatibilidad)

---

## [FASE-6] Docker

### Añadido
- `backend/Dockerfile` — eclipse-temurin:21-jre-alpine
- `frontend-angular/Dockerfile` — multi-stage: node:20-alpine build + nginx:alpine serve
- `frontend-angular/nginx.conf` — SPA fallback + cache headers para assets estáticos
- `docker-compose.yml` — 3 servicios (sqlserver, backend, frontend) con healthchecks ordenados
- Volumen nombrado `sqlserver_data` para persistencia de datos

---

## [FASE-7] Tests

### Añadido
- `WhatsAppProviderTest` — 6 casos unit: mock no lanza, isEnabled=false, null phone
- `ReservaServiceConcurrencyTest` — documenta race condition conocida (sin @Lock)
- `ReservaServiceImplTest.testCancelarReserva` — verifica bug fix (restauración de cupos)
- `ReservaServiceImplTest.testCancelarReservaYaCancelada` — doble cancelación lanza excepción
- Angular: `auth.service.spec.ts`, `auth.interceptor.spec.ts`, `admin.guard.spec.ts` (10/10 pass)

---

## Pendiente (fuera de alcance de esta rama)

- Implementación completa de `MetaWhatsAppCloudProvider` (Graph API calls)
- `@Lock(PESSIMISTIC_WRITE)` en `InventarioPaqueteRepository` para race condition
- Tests E2E con Playwright
- Fix de fallos FK en tests de integración pre-existentes
- `UsuarioService.update()` e implementación de `PaqueteService.activarPaquete()`
