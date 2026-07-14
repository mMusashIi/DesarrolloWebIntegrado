# Credenciales y variables de entorno por servicio

## Regla general

Cada servicio recibe solo las variables que necesita. `JWT_SECRET` es compartido porque todos validan tokens emitidos por `auth-service`. `INTERNAL_SERVICE_TOKEN` es compartido solo por los servicios que participan en llamadas internas mutables.

## Distribucion

| Variable | gateway | auth | catalogo | inventario | reserva | pago | notificacion | reporte |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| `JWT_SECRET` | si | si | si | si | si | si | si | si |
| `JWT_EXPIRATION` | no | si | si | si | si | si | no | si |
| `INTERNAL_SERVICE_TOKEN` | no | no | no | si | si | si | si | no |
| `DB_URL` | no | BuganvillaAuth | BuganvillaCatalogo | BuganvillaInventario | BuganvillaReservas | BuganvillaPagos | no | no |
| `DB_USERNAME` | no | si | si | si | si | si | no | no |
| `DB_PASSWORD` | no | si | si | si | si | si | no | no |
| `AUTH_SERVICE_URL` | si | no | no | no | no | no | no | no |
| `CATALOGO_SERVICE_URL` | si | no | no | no | no | no | no | no |
| `INVENTARIO_SERVICE_URL` | si | no | no | no | si | no | no | no |
| `RESERVA_SERVICE_URL` | si | no | no | no | no | si | no | si |
| `PAGO_SERVICE_URL` | si | no | no | no | no | no | no | si |
| `NOTIFICACION_SERVICE_URL` | si | no | no | no | si | si | no | no |
| `REPORTE_SERVICE_URL` | si | no | no | no | no | no | no | no |
| `MP_*` | no | no | no | no | no | si | no | no |
| `WHATSAPP_*` | no | no | no | no | no | no | si | no |
| `APIS_NET_TOKEN` | no | no | no | no | no | no | si | no |
| `CORS_ALLOWED_ORIGINS` | no | si | si | si | si | si | si | si |

## Valores minimos

```bash
DB_PASSWORD=tu_contrasena
JWT_SECRET=valor_largo_aleatorio
JWT_EXPIRATION=86400000
INTERNAL_SERVICE_TOKEN=valor_largo_aleatorio_distinto_al_jwt
CORS_ALLOWED_ORIGINS=http://localhost,http://localhost:80,http://localhost:4200
```

## Bases SQL Server

El Compose crea estas bases con `docker/init.sql`:

```sql
CREATE DATABASE BuganvillaAuth;
CREATE DATABASE BuganvillaCatalogo;
CREATE DATABASE BuganvillaInventario;
CREATE DATABASE BuganvillaReservas;
CREATE DATABASE BuganvillaPagos;
```

## Servicios sin base de datos

- `api-gateway`
- `notificacion-service`
- `reporte-service`

## Desarrollo con H2

Los servicios con JPA tienen `application-dev.properties` con H2 en memoria. Para tests:

```bash
./mvnw.cmd test "-Dspring.profiles.active=dev"
```
