# Diagnóstico General — Buganvilla Tours

**Fecha:** 2026-07-10  
**Rama analizada:** `feature/resynced` (commit `0824bd3`)  
**Analista:** Claude Sonnet 4.6 — Fase 0-1 de modernización integral

---

## 1. Resumen Ejecutivo

Buganvilla Tours es un sistema de reservas de paquetes turísticos (Cusco, Perú). El repositorio contiene un frontend React/Vite y un backend Spring Boot 3.4.11 en Java 21. El proyecto está funcional a nivel de compilación pero presenta problemas de seguridad críticos, un bug de integridad de datos en la cancelación de reservas, ausencia de contenedores Docker y una integración de WhatsApp no oficial.

**Veredicto baseline:** NO APTO PARA PRODUCCIÓN hasta corregir bloqueantes (ver REGISTRO_DE_RIESGOS.md).

---

## 2. Árbol de Directorios (Primer y Segundo Nivel)

```
DesarrolloWebIntegrado/
├── backend/                          Spring Boot 3.4.11 + Java 21
│   ├── src/main/java/com/buganvilla/buganvillatours/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── model/ (dto/, entity/, mapper/)
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/ (impl/)
│   │   └── util/
│   ├── src/main/resources/
│   ├── src/test/
│   ├── pom.xml
│   └── mvnw / mvnw.cmd
├── frontend/                         React 19.2 + Vite 7.2.2
│   ├── src/
│   │   ├── assets/
│   │   ├── components/ (admin/, auth/, common/, home/, packages/, reservations/)
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── styles/
│   │   └── utils/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
└── docs/                             [CREADO en esta fase]
    ├── diagnostico/
    ├── migracion/
    ├── whatsapp/
    └── testing/
```

**AUSENTES (deben crearse):**
- Ningún `Dockerfile` en ninguna carpeta
- Ningún `docker-compose.yml`
- Ningún `.env` ni `.env.example`
- Ningún `AGENTS.md`

---

## 3. Versiones Identificadas

| Componente | Versión |
|-----------|---------|
| Java | 21.0.9 LTS |
| Spring Boot | 3.4.11 |
| Maven Wrapper | 3.9.11 |
| Node.js (sistema) | 25.9.0 |
| npm | 11.12.1 |
| React | 19.2.0 |
| Vite | 7.2.2 |
| Bootstrap | 5.3.8 |
| Axios | 1.13.2 |
| React Router DOM | 7.9.6 |
| JWT (jjwt) | 0.11.5 |
| MapStruct | 1.5.5.Final |
| Lombok | 1.18.36 |
| MercadoPago SDK | 2.9.2 |
| Apache POI | 5.2.3 |
| PDFBox | 3.0.0 |

---

## 4. Arquitectura Actual (Resumen)

```
[Usuario] → [Frontend React :3000] → proxy /api → [Backend Spring Boot :8080]
                                                           ↓
                                              [SQL Server :1433 / H2 en dev]
                                                           
Integraciones externas:
  → MercadoPago API (pagos online)
  → apis.net.pe (consulta DNI por RENIEC)
  → OpenWA (WhatsApp — NO OFICIAL, debe reemplazarse)
```

---

## 5. Resultados de Builds Baseline

| Artefacto | Comando | Resultado | Duración |
|-----------|---------|-----------|---------|
| Frontend React | `npm run build` | ✅ SUCCESS | 1.67s |
| Backend JAR | `./mvnw package -DskipTests` | ✅ SUCCESS | 3.38s |
| Tests Backend | `./mvnw test` | ❌ 44 ERRORS / 130 tests | ~30s |

**Causa de fallos en tests:** Los tests de integración intentan cargar el contexto Spring con la configuración default (SQL Server en localhost:1433). SQL Server no está corriendo en el entorno de análisis. Los tests que usan el perfil `dev` (H2) funcionarían, pero no están configurados para ejecutarse de forma aislada.

**Conclusión:** Los errores son pre-existentes y no fueron introducidos en esta fase. Los tests de integración requieren SQL Server activo o reconfiguración para usar H2 en modo test.

---

## 6. Módulos Funcionales

| Módulo | Backend | Frontend | Estado |
|--------|---------|----------|--------|
| Autenticación (JWT) | ✅ Completo | ✅ Completo | Funcional |
| Registro de usuarios | ✅ Completo | ✅ Completo | Funcional |
| Catálogo de paquetes | ✅ Completo | ✅ Completo | Funcional |
| Inventario y cupos | ✅ Completo | ✅ Completo | Funcional |
| Reservas (creación) | ✅ Completo | ✅ Completo | Funcional |
| Cancelación de reservas | ✅ Parcial (BUG: no restaura cupo) | ✅ Completo | **BUG CRÍTICO** |
| Pagos con MercadoPago | ✅ Completo | ✅ Completo | Funcional |
| Panel admin — paquetes CRUD | ✅ Completo | ✅ Completo | Funcional |
| Panel admin — reservas | ✅ Completo | ❌ Datos mock | **Incompleto** |
| Panel admin — dashboard | ✅ Completo | ❌ Datos mock | **Incompleto** |
| Reportes Excel/PDF | ✅ Completo | ✅ Parcial (estadísticas mock) | Parcial |
| Consulta DNI (RENIEC) | ✅ Completo | ✅ Completo | Funcional |
| Notificaciones WhatsApp | ✅ Implementado (OpenWA no oficial) | N/A | **No oficial** |
| Gestión de lugares | ✅ Completo | ❌ No tiene UI dedicada | Incompleto |
| Gestión de usuarios (admin) | ✅ Parcial (falta update) | ❌ No tiene UI | Incompleto |
| Docker/Contenedores | ❌ No existe | ❌ No existe | **Ausente** |

---

## 7. Problemas Críticos Encontrados

1. **[CRÍTICO] Bug: cancelarReserva() no restaura cupo** — `ReservaServiceImpl.cancelarReserva()` llama `reserva.cancelar()` pero no llama `inventarioPaqueteService.aumentarCupo()`. Los cupos se pierden permanentemente al cancelar.

2. **[CRÍTICO] Secretos en repositorio** — JWT secret, DB password, tokens de APIs externas están en `application.properties` y `application-dev.properties`, archivos versionados en git.

3. **[ALTO] Race condition en reservas** — Sin control de concurrencia (sin `@Version` ni `@Lock`), dos requests simultáneas pueden sobrepasar el cupo disponible.

4. **[ALTO] WhatsApp no oficial** — Se usa OpenWA (automatización de WhatsApp Web), que viola términos de servicio de Meta y puede ser bloqueado sin previo aviso.

5. **[ALTO] Sin GlobalExceptionHandler** — Errores no controlados devuelven stacktraces al cliente, filtrando información interna.

6. **[ALTO] CORS abierto a `*`** — Cualquier origen puede hacer requests a la API.

7. **[MEDIO] Tests de integración no aislados** — Requieren SQL Server; no hay perfil de test configurado con H2.

8. **[MEDIO] Panel admin con datos mock** — `Dashboard.jsx` y `ReservationsManagement.jsx` usan datos hardcodeados.

---

## 8. Próximas Acciones

1. Escribir documentos detallados de diagnóstico (Fases 0-1 completas)
2. Crear rama de trabajo: `refactor/angular-backend-whatsapp` ✅
3. Crear tag `baseline-v1.0` ✅
4. Ejecutar migración Angular (Fase 3)
5. Corregir bugs backend críticos (Fase 4)
6. Reemplazar WhatsApp (Fase 5)
7. Crear Docker (Fase 6)
8. Tests completos (Fase 7)
