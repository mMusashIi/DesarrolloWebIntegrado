# Arquitectura de microservicios - Buganvilla Tours

El backend monolitico de `backend/` se separo en 8 componentes:

- `api-gateway`
- `auth-service`
- `catalogo-service`
- `inventario-service`
- `reserva-service`
- `pago-service`
- `notificacion-service`
- `reporte-service`

`backend/` queda como referencia del monolito original. El despliegue actualizado usa los servicios nuevos.

## Servicios y puertos

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---:|---|---|
| api-gateway | 8080 | - | Entrada HTTP, rutas `/api/**`, validacion JWT basica |
| auth-service | 8081 | BuganvillaAuth | Login, registro, usuarios y emision JWT |
| catalogo-service | 8082 | BuganvillaCatalogo | Paquetes y lugares |
| inventario-service | 8083 | BuganvillaInventario | Cupos y salidas |
| reserva-service | 8084 | BuganvillaReservas | Reservas |
| pago-service | 8085 | BuganvillaPagos | Pagos y MercadoPago |
| notificacion-service | 8086 | - | WhatsApp y consultas DNI/RUC |
| reporte-service | 8087 | - | Reportes Excel/PDF |

## Contratos de seguridad

- `auth-service` emite JWT con `sub` = email, `idUsuario` y `rol`.
- Los servicios consumidores reconstruyen el usuario desde claims firmados, no desde una BD local de usuarios.
- Las operaciones internas mutables usan `X-Internal-Token` con `INTERNAL_SERVICE_TOKEN`.
- El gateway enruta con propiedades `spring.cloud.gateway.server.webmvc.routes[*]` y URLs por variables `*_SERVICE_URL`.

## Despliegue Docker

`docker-compose.yml` levanta:

- SQL Server con las bases `BuganvillaAuth`, `BuganvillaCatalogo`, `BuganvillaInventario`, `BuganvillaReservas` y `BuganvillaPagos`.
- Los 8 servicios Spring Boot.
- `frontend-angular` servido por Nginx en `http://localhost:4200`, con proxy `/api/` hacia `api-gateway:8080`.

Comandos:

```bash
docker compose up --build
docker compose down
```

## Desarrollo local sin Docker

Para compilar todos los servicios:

```bash
cd api-gateway && ./mvnw -DskipTests compile
```

Repetir en cada carpeta de servicio, o usar los wrappers `mvnw.cmd` en Windows.

Para correr con H2:

```bash
java -jar target/<servicio>.jar --spring.profiles.active=dev
```

Ver [guia-de-arranque.md](guia-de-arranque.md) para orden de inicio local y variables.

## Documentos relacionados

- [distribucion-de-codigo.md](distribucion-de-codigo.md)
- [bases-de-datos.md](bases-de-datos.md)
- [credenciales-por-servicio.md](credenciales-por-servicio.md)
- [comunicacion-inter-servicios.md](comunicacion-inter-servicios.md)
- [guia-de-arranque.md](guia-de-arranque.md)
