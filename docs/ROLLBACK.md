# Plan de Rollback — Modernización Buganvilla Tours

## Checkpoints disponibles

| Tag/Rama | Descripción |
|----------|-------------|
| `baseline-v1.0` | Estado inicial antes de la modernización |
| `react-backup` | Copia del frontend React (crear antes de eliminar `frontend/`) |
| `master` | Rama principal (producción) |
| `feature/resynced` | Estado del proyecto antes del refactor |
| `refactor/angular-backend-whatsapp` | Rama de trabajo actual |

## Rollback por fase

### Volver al estado inicial (completo)

```bash
git checkout baseline-v1.0
# O para volver a la rama original:
git checkout feature/resynced
```

### Rollback solo del backend

```bash
# Identificar el commit anterior al primer cambio de backend en esta rama
git log --oneline refactor/angular-backend-whatsapp
git checkout <commit-anterior> -- backend/
git add backend/ && git commit -m "revert: rollback backend a estado anterior"
```

### Rollback solo de Angular (mantener React)

```bash
# El frontend React nunca fue eliminado — simplemente redeployar el React existente
cd frontend && npm install && npm run build
# El build de React queda en frontend/dist/
```

### Rollback de WhatsApp

```bash
# Volver a OpenWAService en MercadoPagoService:
git show feature/resynced:backend/src/main/java/com/buganvilla/buganvillatours/service/MercadoPagoService.java > backend/src/main/java/com/buganvilla/buganvillatours/service/MercadoPagoService.java
```

### Rollback de variables de entorno

Si `application.properties` causó problemas, restaurar desde el commit original:
```bash
git show feature/resynced:backend/src/main/resources/application.properties > backend/src/main/resources/application.properties
```

## Datos

**Nunca se modificaron datos en producción** en esta rama. Todos los cambios son de código.
No hay migraciones de esquema en esta rama — la BD sigue siendo compatible.

## Verificación post-rollback

```bash
cd backend && ./mvnw package -DskipTests
curl http://localhost:8080/actuator/health
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"email":"...","password":"..."}'
```
