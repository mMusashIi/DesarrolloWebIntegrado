# Plan de Rollback — Migración Angular

**Fecha:** 2026-07-10

---

## 1. Checkpoint de Seguridad

**Tag git creado antes de cualquier cambio:**
```bash
git tag -a baseline-v1.0 -m "Estado inicial antes de modernizacion integral"
```

**Rama de trabajo:**
```bash
git checkout -b refactor/angular-backend-whatsapp
```

**Ramas de respaldo:**
- `feature/resynced` — estado original del proyecto
- `main` / `master` — también disponibles como referencia

---

## 2. Rollback por Fase

### Fase 4 — Rollback de cambios al backend

Si algún cambio al backend rompe funcionalidad existente:

```bash
# Ver qué archivos cambiaron en esta fase
git log --oneline --diff-filter=M backend/

# Revertir un archivo específico al estado del tag
git checkout baseline-v1.0 -- backend/src/main/java/.../SecurityConfig.java

# Revertir todos los cambios del backend a baseline
git checkout baseline-v1.0 -- backend/
```

**Verificar que el frontend React sigue funcionando:**
```bash
cd frontend && npm run dev
# Probar manualmente: login, crear reserva, admin panel
```

---

### Fase 5 — Rollback de integración WhatsApp

Si la nueva integración causa problemas:

```bash
# El OpenWAService.java se mantuvo (no se eliminó) — solo reactivarlo
# En MercadoPagoService.java, cambiar referencia a OpenWAService
# O simplemente: configurar WHATSAPP_PROVIDER=mock en variables de entorno
```

---

### Fase 6 — Rollback de Docker

Los Dockerfiles son archivos nuevos — simplemente no usarlos o eliminarlos:

```bash
git rm docker-compose.yml backend/Dockerfile frontend-angular/Dockerfile
```

---

### Fase 3 — Rollback de Angular

El frontend Angular está en `frontend-angular/` (carpeta nueva). React está en `frontend/` (sin tocar).

Para deshacer el frontend Angular completamente:
```bash
git rm -r frontend-angular/
```

Para volver al frontend React:
```bash
cd frontend && npm install && npm run dev
# El backend sigue siendo el mismo — React funciona igual
```

---

## 3. Rollback Total (Volver a Baseline)

Para volver al estado exacto del inicio:

```bash
# Opción 1: Volver a la rama original
git checkout feature/resynced

# Opción 2: Usar el tag de baseline
git checkout baseline-v1.0

# Opción 3: Crear rama nueva desde baseline
git checkout -b rollback-$(date +%Y%m%d) baseline-v1.0
```

---

## 4. Protección de Datos en BD

**Reglas innegociables:**
- NUNCA ejecutar `DROP TABLE`, `TRUNCATE` o borrar columnas en producción
- Las migraciones SQL propuestas son ADITIVAS (ADD CONSTRAINT, CREATE INDEX)
- Si una migración falla: `ALTER TABLE ... DROP CONSTRAINT <nombre>` la revierte
- `ddl-auto=update` de Hibernate solo AGREGA — nunca borra

**Si una migración de BD causa problemas:**
```sql
-- Ejemplo de rollback para constraint de cupo negativo
ALTER TABLE inventario_paquetes DROP CONSTRAINT CHK_cupo_disponible_no_negativo;
```

---

## 5. Estrategia de Rama React-Backup

Antes de reemplazar definitivamente el frontend React por Angular:

```bash
# Crear rama que preserva React como referencia permanente
git checkout -b react-backup
git push origin react-backup

# Crear tag de la versión React
git tag -a react-v1.0-final -m "Ultima version del frontend React antes de reemplazo"
```

Este paso SOLO se ejecuta después de confirmar paridad funcional completa en Angular.

---

## 6. Verificación de Rollback

Tras cualquier rollback, verificar:

```bash
# 1. Build de React funciona
cd frontend && npm run build

# 2. Backend compila
cd backend && ./mvnw package -DskipTests

# 3. Conectividad API (si hay SQL Server disponible)
curl http://localhost:8080/api/paquetes/activos

# 4. Login funciona
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@buganvillatours.com","password":"1234"}'
```
