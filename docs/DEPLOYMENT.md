# Guia de despliegue - Buganvilla Tours

Este despliegue usa la arquitectura actual de microservicios. El monolito en
`backend/` queda como referencia historica y no forma parte del
`docker-compose.yml` principal.

## Prerrequisitos

- Docker Desktop 4.x o Docker Engine 24+
- Docker Compose v2
- Git
- Puertos libres: `4200`, `1433`, `8080` a `8087`

## Variables de entorno

Para desarrollo local no es necesario crear ni modificar `.env`: Docker Compose incluye valores predeterminados
para base de datos, JWT, comunicación interna, CORS y URLs entre servicios.

Para producción o integraciones externas reales, copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

Ajustar como mínimo:

```env
DB_PASSWORD=<password-fuerte-para-sqlserver>
JWT_SECRET=<secreto-jwt-de-256-bits-o-mas>
INTERNAL_SERVICE_TOKEN=<token-largo-para-comunicacion-interna>
MP_ACCESS_TOKEN=<token-real-si-se-usa-mercadopago>
```

No usar los valores `dev-*` en produccion.

## Levantar el stack

Desde la raiz del repositorio:

```bash
docker compose up --build -d
```

Esto levanta:

| Servicio | Host | Responsabilidad |
|---|---|---|
| `sqlserver` | `localhost:1433` | Bases de datos |
| `api-gateway` | `localhost:8080` | Entrada HTTP `/api/**` |
| `auth-service` | `localhost:8081` | Login, registro, usuarios |
| `catalogo-service` | `localhost:8082` | Paquetes y lugares |
| `inventario-service` | `localhost:8083` | Cupos y salidas |
| `reserva-service` | `localhost:8084` | Reservas |
| `pago-service` | `localhost:8085` | Pagos y MercadoPago |
| `notificacion-service` | `localhost:8086` | WhatsApp y DNI/RUC |
| `reporte-service` | `localhost:8087` | Reportes |
| `frontend-angular` | `localhost:4200` | UI Angular con proxy a gateway |

## Verificacion basica

```bash
docker compose ps
docker compose logs --tail=100 api-gateway
curl http://localhost:8080/api/auth/check
curl http://localhost:4200/api/auth/check
```

Login de prueba si se cargaron los seeds del servicio de auth:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@buganvilla.com","password":"admin123"}'
```

## Logs utiles

```bash
docker compose logs -f api-gateway
docker compose logs -f auth-service
docker compose logs -f reserva-service
docker compose logs -f pago-service
docker compose logs -f frontend-angular
docker compose logs -f sqlserver
```

## Actualizar despliegue

```bash
git pull
docker compose build
docker compose up -d
```

Para reconstruir solo un servicio:

```bash
docker compose build reserva-service
docker compose up -d reserva-service
```

## Detener

```bash
docker compose down
```

Eliminar tambien datos de SQL Server:

```bash
docker compose down -v
```

Ese ultimo comando destruye las bases locales del volumen Docker.
