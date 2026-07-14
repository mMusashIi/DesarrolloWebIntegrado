# Registro de Riesgos — Buganvilla Tours

**Fecha:** 2026-07-10  
**Escala:** BLOQUEANTE > CRÍTICO > ALTO > MEDIO > BAJO > MEJORA OPCIONAL

---

## Riesgos Críticos (Deben corregirse antes de producción)

| ID | Riesgo | Severidad | Probabilidad | Impacto | Fase Corrección |
|----|--------|-----------|-------------|---------|----------------|
| R01 | cancelarReserva() no restaura cupos | CRÍTICO | Alta | Cupos bloqueados permanentemente; sistema dice "sin cupo" aunque haya disponibilidad | Fase 4 |
| R02 | Secretos en repositorio git | CRÍTICO | Media (ya ocurrió) | Acceso no autorizado a BD, APIs de pago, JWT forgery | Fase 4 |
| R03 | Race condition en creación de reservas | ALTO | Media (uso concurrente) | Cupos negativos; sobreventa de tours | Fase 4 |
| R04 | Webhook MercadoPago sin firma | ALTO | Baja | Confirmación de reservas sin pago real; pérdida de ingresos | Fase 4 |
| R05 | WhatsApp no oficial (OpenWA) | ALTO | Alta | Bloqueo del número sin aviso; pérdida de canal de comunicación | Fase 5 |
| R06 | Sin GlobalExceptionHandler | ALTO | Alta | Stacktraces expuestos; info interna de la app revelada a usuarios | Fase 4 |
| R07 | CORS abierto a `*` | ALTO | Media | Requests cross-origin desde cualquier sitio con credenciales del usuario | Fase 4 |

---

## Riesgos Medios (Deben planificarse)

| ID | Riesgo | Severidad | Probabilidad | Impacto | Fase Corrección |
|----|--------|-----------|-------------|---------|----------------|
| R08 | Tests de integración no pasan (SQL Server requerido) | MEDIO | Alta en CI | Regresiones no detectadas; deploy sin validación | Fase 7 |
| R09 | Panel admin con datos mock | MEDIO | N/A | Administradores ven datos falsos; decisiones basadas en información incorrecta | Fase 3 |
| R10 | Sin TypeScript en frontend | MEDIO | Media | Bugs de tipado difíciles de detectar; errores en runtime | Fase 3 (Angular con TS) |
| R11 | Validación de inputs débil en backend | MEDIO | Media | Datos inválidos en BD; posibles errores en runtime | Fase 4 |
| R12 | N+1 queries en reportes | MEDIO | Media (con volumen) | Rendimiento degradado con muchas reservas | Fase 4 |
| R13 | show-sql=true en producción | MEDIO | Alta | Datos sensibles en logs; llenado de disco | Fase 4 |
| R14 | ddl-auto=update en producción | MEDIO | Baja | Cambios en entidades pueden alterar el esquema no intencionalmente | Fase 4 |
| R15 | Sin restricciones CHECK en BD | MEDIO | Baja | Estados inválidos pueden insertarse directamente en BD | Fase 4 |
| R16 | Sin UNIQUE constraint en (paquete, fecha_salida) | MEDIO | Baja | Duplicados en inventario; cupos duplicados | Fase 4 |

---

## Riesgos Bajos (Mejoras)

| ID | Riesgo | Severidad | Probabilidad | Impacto | Fase Corrección |
|----|--------|-----------|-------------|---------|----------------|
| R17 | Auto-login en RegisterModal tiene bug | BAJO | Alta | UX degradada; usuario debe hacer login manual tras registro en modal | Fase 3 |
| R18 | Código duplicado (RegisterModal en 2 lugares) | BAJO | N/A | Mantenimiento más costoso | Fase 3 |
| R19 | Métodos sin implementar en servicios | BAJO | Media | Features que parecen disponibles pero fallan en runtime | Fase 4 |
| R20 | Sin Docker/contenedores | BAJO | N/A | Deploy manual más propenso a errores; no reproducible | Fase 6 |
| R21 | Sin índice en fecha_salida del inventario | BAJO | Baja | Queries lentas con muchos registros de inventario | Fase 4 |
| R22 | Log level DEBUG en producción | BAJO | Alta | Logs muy verbosos; impacto en rendimiento | Fase 4 |

---

## Mejoras Opcionales

| ID | Descripción |
|----|------------|
| M01 | Paginación en listados grandes (reservas, usuarios) |
| M02 | Health endpoint con Actuator |
| M03 | Rate limiting en endpoint de login |
| M04 | Caché en catálogo de paquetes (lista pública frecuente) |
| M05 | Funcionalidad de reprogramación de reservas (actualmente ausente) |
| M06 | Formulario de contacto real (backend endpoint) |
| M07 | Notificaciones por email (además de WhatsApp) |

---

## Tabla de Mitigación por Fase

| Fase | Riesgos que mitiga |
|------|-------------------|
| Fase 3 — Angular | R09 (admin con datos reales), R10 (TypeScript), R17, R18 |
| Fase 4 — Backend | R01, R02, R03, R04, R06, R07, R11, R12, R13, R15, R16, R19 |
| Fase 5 — WhatsApp | R05 |
| Fase 6 — Docker | R20 |
| Fase 7 — Tests | R08 |

---

## Criterio de Producción

El proyecto NO debe ir a producción hasta que los riesgos R01 a R07 estén corregidos.

| Riesgo | Criterio de cierre |
|--------|-------------------|
| R01 | Test de cancelación que verifica restauración de cupo |
| R02 | Secretos en variables de entorno; `.env.example` en repo; credenciales rotadas |
| R03 | Test de concurrencia (2 requests simultáneas al mismo cupo) |
| R04 | Validación de firma en webhook con test de firma inválida |
| R05 | OpenWAService reemplazado por WhatsApp Business Cloud API (proveedor mock activo) |
| R06 | GlobalExceptionHandler creado; tests verifican formato de error sin stacktrace |
| R07 | Lista de orígenes CORS definida por variable de entorno; test de origen no autorizado |
