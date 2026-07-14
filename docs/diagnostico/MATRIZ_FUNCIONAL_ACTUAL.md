# Matriz Funcional Actual — Buganvilla Tours

**Fecha:** 2026-07-10

---

## Estado por Funcionalidad

| ID | Funcionalidad | Backend | Frontend | Estado Global | Notas |
|----|--------------|---------|----------|--------------|-------|
| F01 | Registro de usuario | ✅ Completo | ✅ Completo | **FUNCIONAL** | Auto-login funciona en /register; bug en modal |
| F02 | Login / Logout | ✅ Completo | ✅ Completo | **FUNCIONAL** | JWT 24h, roles admin/cliente |
| F03 | Perfil de usuario (ver) | ✅ Completo | ⚠️ Parcial | **PARCIAL** | GET /auth/profile existe; sin UI de perfil dedicada |
| F04 | Edición de perfil | ❌ No existe PUT /usuarios/{id} para self-edit | ❌ Sin UI | **AUSENTE** | Solo admin puede gestionar usuarios |
| F05 | Catálogo de paquetes | ✅ Completo | ✅ Completo | **FUNCIONAL** | Grid con filtros, modal de detalles |
| F06 | Filtros de búsqueda de paquetes | ✅ Completo | ✅ Completo | **FUNCIONAL** | Por nombre, precio, ciudad |
| F07 | Detalle de paquete | ✅ Completo | ✅ Completo | **FUNCIONAL** | Modal con info completa e inventario |
| F08 | Ver inventario y cupos | ✅ Completo | ✅ Completo | **FUNCIONAL** | Por paquete y fecha de salida |
| F09 | Próximas salidas | ✅ Completo | ✅ Parcial | **PARCIAL** | Backend tiene endpoint; frontend usa disponible |
| F10 | Crear reserva | ✅ Completo | ✅ Completo | **FUNCIONAL** | Con consulta DNI RENIEC |
| F11 | Ver mis reservas | ✅ Completo | ⚠️ Parcial | **PARCIAL** | Endpoint existe; sin página dedicada para cliente |
| F12 | Cancelar reserva | ✅ Endpoint existe pero con BUG | ✅ Tiene botón | **BUG CRÍTICO** | No restaura cupo al cancelar |
| F13 | Reprogramar reserva | ❌ No existe | ❌ No existe | **AUSENTE** | Mencionado en spec pero no implementado |
| F14 | Pago con MercadoPago | ✅ Completo | ✅ Completo | **FUNCIONAL** | Preferencia + webhook + redirects |
| F15 | Confirmación de pago | ✅ Completo | ✅ Completo | **FUNCIONAL** | Webhook actualiza reserva y pago |
| F16 | Notificación WhatsApp | ✅ Implementado (no oficial) | N/A | **NO OFICIAL** | OpenWA — puede fallar en cualquier momento |
| F17 | Panel admin — Dashboard | ✅ Endpoints existen | ❌ MOCK | **INCOMPLETO** | Frontend usa Math.random(), no conecta API |
| F18 | Panel admin — CRUD Paquetes | ✅ Completo | ✅ Completo | **FUNCIONAL** | Crear, editar, eliminar, activar/desactivar |
| F19 | Panel admin — Gestión Inventario | ✅ Completo | ✅ Completo | **FUNCIONAL** | Cupos por paquete y fecha |
| F20 | Panel admin — Ver Reservas | ✅ Completo | ❌ MOCK | **INCOMPLETO** | Frontend muestra datos hardcodeados |
| F21 | Panel admin — Procesar Pagos | ✅ Completo | ⚠️ Parcial | **PARCIAL** | Endpoint PUT /pagos/{id}/procesar existe; UI básica |
| F22 | Panel admin — Gestión Usuarios | ✅ Parcial (falta update) | ❌ No existe | **INCOMPLETO** | Sin UI; service.update() no implementado |
| F23 | Panel admin — Gestión Lugares | ✅ Completo | ❌ No existe | **INCOMPLETO** | Sin UI para lugares |
| F24 | Reportes Excel | ✅ Completo | ✅ Funcional | **FUNCIONAL** | Descarga /api/reportes/excel |
| F25 | Reportes PDF | ✅ Completo | ✅ Funcional | **FUNCIONAL** | Descarga /api/reportes/pdf |
| F26 | Consulta DNI (RENIEC) | ✅ Completo | ✅ Completo | **FUNCIONAL** | Autocompletado en formulario de reserva |
| F27 | Formulario de contacto | ❌ No existe | ❌ Simulado | **AUSENTE** | setTimeout fake, sin backend |
| F28 | Gestión de transportes | ❌ No existe | ❌ No existe | **AUSENTE** | Componente vacío |
| F29 | Inventario de productos | ❌ No existe | ❌ No existe | **AUSENTE** | Componente vacío |
| F30 | Responsive / mobile | N/A | ✅ Bootstrap | **FUNCIONAL** | Bootstrap 5 con diseño responsive |
| F31 | Docker / contenedores | ❌ No existe | ❌ No existe | **AUSENTE** | Debe crearse completamente |

---

## Resumen por Estado

| Estado | Cantidad |
|--------|---------|
| FUNCIONAL | 14 |
| PARCIAL | 5 |
| INCOMPLETO | 6 |
| BUG CRÍTICO | 1 |
| NO OFICIAL | 1 |
| AUSENTE | 4 |
| **TOTAL** | **31** |

---

## Funcionalidades Prioritarias para Angular (Fase 3)

Las siguientes funcionalidades DEBEN tener paridad en Angular antes de cualquier reemplazo:

### Prioridad 1 — CRÍTICAS
1. F01 — Registro + auto-login (corregir bug del modal)
2. F02 — Login / Logout
3. F05 — Catálogo de paquetes
4. F06 — Filtros de búsqueda
5. F07 — Detalle de paquete
6. F08 — Inventario y cupos
7. F10 — Crear reserva (con DNI)
8. F14 — Pago MercadoPago
9. F18 — Panel admin — CRUD paquetes
10. F19 — Panel admin — Inventario

### Prioridad 2 — IMPORTANTES
11. F11 — Ver mis reservas (implementar UI real)
12. F17 — Dashboard admin (con datos reales, no mock)
13. F20 — Reservas admin (con datos reales, no mock)
14. F24 — Reportes Excel
15. F25 — Reportes PDF

### Prioridad 3 — MEJORAS RESPECTO AL ESTADO ACTUAL
16. F03 — Perfil de usuario (UI dedicada)
17. F12 — Cancelar reserva (con fix de cupo)
18. F22 — Gestión de usuarios admin
19. F23 — Gestión de lugares admin

### No incluir en primera iteración Angular (fuera de scope actual)
- F13 — Reprogramar reserva (no existe en backend)
- F27 — Formulario de contacto real
- F28 — Transportes
- F29 — Inventario de productos

---

## Roles y Accesos

| Funcionalidad | Público | Cliente | Admin |
|--------------|---------|---------|-------|
| Ver catálogo y paquetes | ✅ | ✅ | ✅ |
| Ver inventario | ✅ | ✅ | ✅ |
| Consulta DNI | ✅ | ✅ | ✅ |
| Registro / Login | ✅ | ✅ | ✅ |
| Crear reserva | ❌ | ✅ | ✅ |
| Ver mis reservas | ❌ | ✅ | ✅ |
| Cancelar reserva | ❌ | ✅ (propia) | ✅ |
| Iniciar pago MP | ❌ | ✅ | ✅ |
| Panel admin | ❌ | ❌ | ✅ |
| CRUD paquetes | ❌ | ❌ | ✅ |
| Gestión inventario | ❌ | ❌ | ✅ |
| Ver todas las reservas | ❌ | ❌ | ✅ |
| Reportes | ❌ | ❌ | ✅ |
| Gestión usuarios | ❌ | ❌ | ✅ |
