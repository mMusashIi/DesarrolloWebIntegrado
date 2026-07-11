# Comunicacion entre microservicios

Los servicios usan `RestClient` (Spring 6) para llamadas HTTP sincronas. No hay broker de mensajes ni descubrimiento dinamico de servicios.

## Mapa de dependencias

```text
reserva-service -> inventario-service    (verificar/reducir/aumentar cupo)
reserva-service -> notificacion-service  (notificar reserva/cancelacion)
pago-service    -> reserva-service       (confirmar/cancelar reserva)
pago-service    -> notificacion-service  (notificar pago)
reporte-service -> reserva-service       (obtener reservas)
reporte-service -> pago-service          (obtener pagos por reserva)
```

## Seguridad interna

Las llamadas internas mutables usan el header:

```http
X-Internal-Token: <INTERNAL_SERVICE_TOKEN>
```

`INTERNAL_SERVICE_TOKEN` debe tener el mismo valor en:

- `reserva-service`
- `inventario-service`
- `pago-service`
- `notificacion-service`

El gateway no marca `/api/notificacion/**` ni las rutas internas de inventario como publicas. Aunque una ruta este definida en el gateway, los endpoints internos validan `X-Internal-Token` en el servicio receptor.

## Detalle de llamadas

### reserva-service -> inventario-service

Variable: `INVENTARIO_SERVICE_URL` (local: `http://localhost:8083`, Docker: `http://inventario-service:8083`).

| Metodo | Endpoint | Proposito | Seguridad |
|---|---|---|---|
| GET | `/api/inventario/{id}/verificar?cantidad=N` | Verificar cupo disponible | `X-Internal-Token` |
| PUT | `/api/inventario/{id}/reducir-cupo?cantidad=N` | Reducir cupo al crear reserva | `X-Internal-Token` |
| PUT | `/api/inventario/{id}/aumentar-cupo?cantidad=N` | Liberar cupo al cancelar | `X-Internal-Token` |

### reserva-service -> notificacion-service

Variable: `NOTIFICACION_SERVICE_URL` (local: `http://localhost:8086`, Docker: `http://notificacion-service:8086`).

| Metodo | Endpoint | Body | Seguridad |
|---|---|---|---|
| POST | `/api/notificacion/reserva` | `{phone, name, reservaId}` | `X-Internal-Token` |
| POST | `/api/notificacion/cancelacion` | `{phone, name, packageName}` | `X-Internal-Token` |

Estas llamadas son fire-and-forget: si fallan, se registra `warn` y no se interrumpe el flujo principal.

### pago-service -> reserva-service

Variable: `RESERVA_SERVICE_URL` (local: `http://localhost:8084`, Docker: `http://reserva-service:8084`).

| Metodo | Endpoint | Proposito | Seguridad |
|---|---|---|---|
| PUT | `/api/reservas/{id}/confirmar` | Confirmar reserva tras pago aprobado | `X-Internal-Token` o JWT ADMIN |
| PUT | `/api/reservas/{id}/cancelar` | Cancelar reserva tras pago rechazado | `X-Internal-Token` o JWT ADMIN |

### pago-service -> notificacion-service

Variable: `NOTIFICACION_SERVICE_URL`.

| Metodo | Endpoint | Body | Seguridad |
|---|---|---|---|
| POST | `/api/notificacion/pago` | `{phone, name, amount, reservaId}` | `X-Internal-Token` |

### reporte-service -> reserva-service

Variable: `RESERVA_SERVICE_URL`.

| Metodo | Endpoint | Proposito | Seguridad |
|---|---|---|---|
| GET | `/api/reservas` | Obtener todas las reservas | Reenvia JWT ADMIN del usuario |
| GET | `/api/reservas/usuario/{id}` | Obtener reservas de usuario | Reenvia JWT del usuario |

### reporte-service -> pago-service

Variable: `PAGO_SERVICE_URL`.

| Metodo | Endpoint | Proposito | Seguridad |
|---|---|---|---|
| GET | `/api/pagos/reserva/{id}` | Obtener pagos de una reserva | Reenvia JWT |

## Clientes REST

| Servicio origen | Clase cliente | Servicio destino |
|---|---|---|
| reserva-service | `InventarioClient` | inventario-service |
| reserva-service | `NotificacionClient` | notificacion-service |
| pago-service | `ReservaClient` | reserva-service |
| pago-service | `NotificacionClient` | notificacion-service |
| reporte-service | `ReservaClient` | reserva-service |
| reporte-service | `PagoClient` | pago-service |
