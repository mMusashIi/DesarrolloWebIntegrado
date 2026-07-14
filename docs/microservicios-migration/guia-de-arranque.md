# Guia de arranque de microservicios

## Opcion recomendada: Docker Compose

Desde la raiz del repositorio:

```bash
docker compose up --build
```

Servicios disponibles:

| URL | Servicio |
|---|---|
| `http://localhost:4200` | frontend-angular |
| `http://localhost:8080` | api-gateway |
| `http://localhost:8081` | auth-service |
| `http://localhost:8082` | catalogo-service |
| `http://localhost:8083` | inventario-service |
| `http://localhost:8084` | reserva-service |
| `http://localhost:8085` | pago-service |
| `http://localhost:8086` | notificacion-service |
| `http://localhost:8087` | reporte-service |

SQL Server queda disponible en `localhost:1433`. El script `docker/init.sql` crea las bases necesarias.

## Variables minimas

Crear `.env` desde `.env.example` y definir al menos:

```bash
DB_PASSWORD=...
JWT_SECRET=...
INTERNAL_SERVICE_TOKEN=...
```

`JWT_SECRET` debe ser el mismo para gateway y servicios. `INTERNAL_SERVICE_TOKEN` debe ser el mismo para `reserva-service`, `inventario-service`, `pago-service` y `notificacion-service`.

## Arranque local sin Docker

Para usar H2 en memoria:

```bash
cd auth-service
./mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Orden recomendado:

1. `auth-service` (`8081`)
2. `catalogo-service` (`8082`)
3. `inventario-service` (`8083`)
4. `notificacion-service` (`8086`)
5. `reserva-service` (`8084`)
6. `pago-service` (`8085`)
7. `reporte-service` (`8087`)
8. `api-gateway` (`8080`)
9. `frontend-angular`

Para ejecutar el frontend Angular local:

```bash
cd frontend-angular
npm install
npm start
```

## Verificaciones rapidas

Compilar servicios:

```bash
./mvnw.cmd -DskipTests compile
```

Ejecutar tests con H2:

```bash
./mvnw.cmd test "-Dspring.profiles.active=dev"
```

Validar Compose:

```bash
docker compose config --quiet
```

## Notas

- El monolito en `backend/` ya no es el objetivo del Compose principal.
- Los endpoints internos no deben llamarse desde el navegador. Usan `X-Internal-Token`.
- Los reportes reenvian el JWT recibido para consultar reservas y pagos.
