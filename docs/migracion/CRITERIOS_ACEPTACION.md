# Criterios de Aceptación — Modernización Buganvilla Tours

**Fecha:** 2026-07-10

---

## Criterios de Aceptación — Angular Frontend

- [ ] Angular 18 compila en modo desarrollo (`ng serve` sin errores)
- [ ] Angular 18 compila en modo producción (`ng build` sin errores)
- [ ] TypeScript strict mode habilitado y sin errores de tipado
- [ ] Todas las 7 páginas React tienen equivalente Angular (Home, Packages, Reservations, Admin, About, Contact, Register)
- [ ] Todos los 7 servicios HTTP tienen equivalente Angular tipado
- [ ] AuthInterceptor agrega token en todas las requests autenticadas
- [ ] ErrorInterceptor maneja 401 → logout + redirect
- [ ] AuthGuard protege rutas que requieren autenticación
- [ ] AdminGuard protege rutas que requieren rol admin
- [ ] Formulario de reserva con todas las validaciones
- [ ] Consulta DNI RENIEC funcional en formulario
- [ ] Flujo MercadoPago funcional (crear preferencia → redirect → webhook)
- [ ] Dashboard admin con datos reales (no mock)
- [ ] Gestión de reservas admin con datos reales (no mock)
- [ ] CRUD de paquetes funcional en Angular
- [ ] Gestión de inventario funcional en Angular
- [ ] Descarga de reportes Excel y PDF funcional
- [ ] Responsive: funciona en mobile (< 768px), tablet (768-1024px), desktop (> 1024px)
- [ ] Paleta de colores preservada (violeta #4c1d95, rosado #db2777)
- [ ] Estados de carga, error y contenido vacío en todos los componentes
- [ ] Token no aparece en consola del navegador
- [ ] Sin `any` en TypeScript (excepto con comentario documentado)
- [ ] Matriz de paridad completa (todas las filas en estado "Verificada")

---

## Criterios de Aceptación — Backend

- [ ] Backend compila sin errores (`./mvnw package -DskipTests`)
- [ ] cancelarReserva() restaura cupo correctamente (test incluido)
- [ ] Race condition corregida con lock o versioning (test de concurrencia incluido)
- [ ] GlobalExceptionHandler creado — sin stacktraces en respuestas
- [ ] Webhook MercadoPago valida firma
- [ ] CORS configurado por variable de entorno (no hardcodeado `*`)
- [ ] Secretos en variables de entorno (NO en application.properties)
- [ ] Atomicidad en pago: pago + confirmación de reserva en misma transacción
- [ ] Tests de integración pasan con H2 (`@ActiveProfiles("dev")`)
- [ ] Contratos de API sin cambios (todos los campos de request/response iguales)

---

## Criterios de Aceptación — WhatsApp

- [ ] Interfaz `WhatsAppNotificationProvider` definida
- [ ] `MockWhatsAppProvider` implementado — logs "MOCK" sin envíos reales
- [ ] `MetaWhatsAppCloudProvider` implementado — desactivado por defecto (`WHATSAPP_ENABLED=false`)
- [ ] Selección de proveedor por configuración (`WHATSAPP_PROVIDER=mock|meta`)
- [ ] `OpenWAService` deprecado pero no eliminado
- [ ] MercadoPagoService usa `WhatsAppNotificationService` en vez de `OpenWAService`
- [ ] Webhook de WhatsApp valida firma HMAC-SHA256
- [ ] Tests del proveedor mock pasan
- [ ] Documentación en `docs/whatsapp/` completa

---

## Criterios de Aceptación — Docker

- [ ] `backend/Dockerfile` construye la imagen correctamente
- [ ] `frontend-angular/Dockerfile` construye la imagen correctamente
- [ ] `docker-compose.yml` inicia los 3 servicios (sqlserver, backend, frontend-angular)
- [ ] `docker compose up --build` completa sin errores
- [ ] Frontend Angular accesible en puerto configurado (ej: 80 o 4200)
- [ ] Backend Spring Boot accesible en puerto 8080
- [ ] SQL Server en Docker con volumen nombrado (datos persistentes)
- [ ] Health checks definidos para backend y sqlserver
- [ ] Variables de entorno via `.env` (no secrets en docker-compose.yml)
- [ ] `.env.example` en el repositorio con todos los nombres de variables

---

## Criterios de Aceptación — Pruebas

- [ ] Tests backend pasan con H2 (`./mvnw test -Dspring.profiles.active=dev`)
- [ ] Test de cancelación de reserva verifica restauración de cupo
- [ ] Test de concurrencia (2 requests simultáneas al mismo cupo → solo 1 éxito)
- [ ] Test de webhook con firma inválida → rechazado
- [ ] Tests unitarios de Angular pasan (`ng test --watch=false`)
- [ ] Pruebas E2E: flujo completo registro → login → reservar → cancelar (Playwright)
- [ ] Pruebas E2E: flujo admin → CRUD paquete → ver reservas → reportes
- [ ] Prueba de seguridad: acceso a /admin sin token → redirect

---

## Criterios de Aceptación — Seguridad

- [ ] Ningún secreto en el repositorio (escaneo con `git grep -i "password\|secret\|token" -- "*.properties"`)
- [ ] `.env` en `.gitignore`
- [ ] CORS restringido a orígenes específicos
- [ ] Webhook MercadoPago con validación de firma
- [ ] Webhook WhatsApp con validación de firma
- [ ] Sin stacktraces en respuestas de error
- [ ] GlobalExceptionHandler formatea errores sin información interna

---

## Criterios de Aceptación — Documentación

- [ ] `docs/diagnostico/` — 10 archivos completos
- [ ] `docs/migracion/` — 5 archivos completos
- [ ] `docs/whatsapp/` — 4 archivos completos
- [ ] `docs/testing/TEST_REPORT.md` con evidencia de pruebas ejecutadas
- [ ] `docs/DEPLOYMENT.md` con instrucciones exactas de instalación
- [ ] `docs/ROLLBACK.md` con pasos para revertir
- [ ] `.env.example` con todos los nombres de variables y descripción
- [ ] `CHANGELOG_MIGRACION.md` con lista de cambios por fase

---

## Criterio de Veredicto Final

| Veredicto | Condición |
|-----------|-----------|
| APTO PARA PRODUCCIÓN | Todos los criterios anteriores cumplidos + sin regresiones críticas |
| APTO PARA STAGING | Angular funcional + backend compilado + Docker OK; tests pendientes |
| NO APTO | Algún criterio crítico (secretos, bug cupos, race condition) sin corregir |
