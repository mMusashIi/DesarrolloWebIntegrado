# Plan de rollback - Buganvilla Tours

Este documento aplica al estado actual con 8 microservicios, API Gateway,
SQL Server y frontend Angular.

## Regla principal

Antes de desplegar, crear un tag o rama del estado estable:

```bash
git tag pre-microservices-stable
git push origin pre-microservices-stable
```

Si ya existe un tag equivalente del monolito, usar ese tag como punto de
retorno.

## Rollback completo a un estado Git anterior

```bash
docker compose down
git fetch --all --tags
git checkout <tag-o-commit-estable>
docker compose up --build -d
```

Si el estado anterior usaba el monolito, confirmar que su `docker-compose.yml`
incluya `backend` y no los servicios separados.

## Rollback de un microservicio

Restaurar solo la carpeta afectada desde un commit estable:

```bash
git checkout <commit-estable> -- reserva-service/
docker compose build reserva-service
docker compose up -d reserva-service
```

Repetir con el nombre real del servicio:

- `api-gateway`
- `auth-service`
- `catalogo-service`
- `inventario-service`
- `reserva-service`
- `pago-service`
- `notificacion-service`
- `reporte-service`

## Rollback del frontend Angular

```bash
git checkout <commit-estable> -- frontend-angular/
docker compose build frontend-angular
docker compose up -d frontend-angular
```

## Rollback de configuracion

Si el problema esta en variables de entorno o compose:

```bash
git checkout <commit-estable> -- docker-compose.yml .env.example docker/
docker compose config --quiet
docker compose up -d
```

No versionar `.env` real. Ajustarlo manualmente si cambia el contrato de
variables.

## Verificacion post-rollback

```bash
docker compose ps
curl http://localhost:8080/api/auth/check
curl http://localhost/api/auth/check
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@buganvilla.com","password":"admin123"}'
```

## Datos

El rollback de codigo no revierte datos. Para un entorno local se puede borrar
el volumen con:

```bash
docker compose down -v
```

En staging o produccion, respaldar SQL Server antes de ejecutar cambios de
esquema o restauraciones.
