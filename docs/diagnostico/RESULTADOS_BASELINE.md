# Resultados Baseline — Buganvilla Tours

**Fecha:** 2026-07-10  
**Entorno:** Windows 11 Home 10.0.26200, sin SQL Server activo

---

## 1. Versiones del Entorno

| Herramienta | Versión |
|------------|---------|
| Node.js | 25.9.0 |
| npm | 11.12.1 |
| Java | 21.0.9 LTS |
| Maven (mvnw) | 3.9.11 |
| Maven global | No disponible (solo wrapper) |
| Docker | No instalado en este entorno |
| SQL Server | No activo en este entorno |

---

## 2. Build Frontend React

**Comando:** `cd frontend && npm install && npm run build`

**Resultado:** ✅ SUCCESS

```
> frontend@0.0.0 build
> vite build

vite v7.2.2 building client environment for production...
✓ 439 modules transformed.
dist/index.html                  0.86 kB │ gzip:   0.46 kB
dist/assets/index-D1TmsEY8.css   9.45 kB │ gzip:   2.53 kB
dist/assets/index-J9UwInrl.js  411.31 kB │ gzip: 123.77 kB │ map: 1,975.07 kB
✓ built in 1.67s
```

**Observaciones:**
- 439 módulos transformados sin errores
- Bundle JS de 411KB (123KB gzip) — aceptable para una SPA de este tamaño
- `npm audit` detectó vulnerabilidades menores (no bloqueantes para la build)

---

## 3. Build Backend Spring Boot

**Comando:** `cd backend && ./mvnw package -DskipTests`

**Resultado:** ✅ SUCCESS

```
[INFO] BUILD SUCCESS
[INFO] Total time:  3.381 s
[INFO] Finished at: 2026-07-10T01:37:05-05:00
```

**Artefacto generado:** `backend/target/buganvillatours-0.0.1-SNAPSHOT.jar`

**Observaciones:**
- Compilación limpia sin errores ni advertencias de compilación
- El JAR se genera correctamente con Spring Boot Repackager

---

## 4. Tests Backend

**Comando:** `cd backend && ./mvnw test`

**Resultado:** ❌ BUILD FAILURE

```
[ERROR] Tests run: 130, Failures: 0, Errors: 44, Skipped: 0
[INFO] BUILD FAILURE
```

**Causa raíz:** Los 44 errores son todos por `ApplicationContext failure threshold (1) exceeded`. Los tests de integración intentan cargar el contexto Spring con la configuración default (SQL Server en localhost:1433), pero SQL Server no está activo en este entorno.

**Clasificación de los errores:**
- **No son regresiones introducidas en esta fase** — son errores pre-existentes
- **No son fallos de lógica** — son problemas de infraestructura de test
- Los tests que podrían pasar con H2 no tienen `@ActiveProfiles("dev")` configurado

**Tests afectados (estimación):**
- `TestController/`: 8 archivos de tests de integración (todos afectados por falta de SQL Server)
- `TestService/`: 7 archivos de tests unitarios (algunos podrían pasar con mocks, según implementación)

**Para ejecutar tests correctamente se requiere:**
- SQL Server activo en localhost:1433 con BD `BuganvillaTours1`, O
- Configurar tests con `@ActiveProfiles("dev")` para usar H2

---

## 5. Tests Frontend React

**Comando:** N/A — El proyecto frontend no tiene framework de tests configurado (no hay Jest, Vitest ni similar en `package.json`).

**Resultado:** NO EJECUTADO — no hay tests frontend

**Observación:** La ausencia de tests frontend es una brecha significativa. Se crearán tests en Angular (Fase 3) y se documentará en la Fase 7.

---

## 6. Inicio del Backend (Simulado)

**Comando:** `cd backend && ./mvnw spring-boot:run` — NO ejecutado (requiere SQL Server)

**Simulación con perfil dev:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
Con perfil `dev` y H2, el backend debería iniciar correctamente dado que la build fue exitosa.

**Estado:** NO EJECUTADO — documentado como pendiente hasta tener SQL Server disponible o Docker configurado.

---

## 7. Docker Build

**Resultado:** NO EJECUTADO — No existen Dockerfiles en el repositorio.

**Acción:** Los Dockerfiles se crearán en la Fase 6.

---

## 8. Conectividad a Base de Datos

**Estado:** NO VERIFICADO — SQL Server no disponible en el entorno de análisis.

**Para verificar en entorno con SQL Server:**
```bash
# Verificar conexión
./mvnw spring-boot:run
curl http://localhost:8080/api/paquetes/activos
```

---

## 9. Resumen de Resultados

| Prueba | Resultado | Notas |
|--------|-----------|-------|
| npm install | ✅ OK | Sin errores fatales |
| npm run build (React) | ✅ OK | 439 módulos, 1.67s |
| ./mvnw package -DskipTests | ✅ OK | BUILD SUCCESS, 3.38s |
| ./mvnw test | ❌ FAIL | 44 errores por falta de SQL Server (pre-existente) |
| npm test (frontend) | N/A | No configurado |
| Backend start | N/A | Requiere SQL Server o perfil dev |
| DB connectivity | N/A | SQL Server no activo |
| Docker build | N/A | Dockerfiles no existen |
| E2E tests | N/A | No configurados |

---

## 10. Acciones Post-Baseline

1. Crear Dockerfiles y docker-compose (Fase 6)
2. Configurar tests de integración para usar H2 con `@ActiveProfiles("dev")` (Fase 7)
3. Ejecutar `./mvnw test -Dspring.profiles.active=dev` en entorno con Docker
4. Crear suite de tests E2E con Playwright (Fase 7)
5. Verificar build Angular en modo dev y producción (Fase 3)
