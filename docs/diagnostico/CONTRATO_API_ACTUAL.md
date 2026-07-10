# Contrato API Actual — Buganvilla Tours

**Fecha:** 2026-07-10  
**Versión congelada:** commit `0824bd3`  
**Base URL:** `http://localhost:8080/api`

> Este documento congela los contratos actuales. Angular DEBE adaptarse a estos contratos. Cualquier cambio al backend que altere estos contratos requiere una capa de compatibilidad temporal.

---

## Autenticación

### POST `/auth/login`
**Acceso:** Público

**Request:**
```json
{ "email": "string", "password": "string" }
```

**Response 200:**
```json
{
  "token": "string (JWT Bearer)",
  "usuario": {
    "idUsuario": 1,
    "nombre": "string",
    "apellido": "string",
    "email": "string",
    "telefono": "string",
    "nacionalidad": "string",
    "dni": "string",
    "rol": "admin | cliente",
    "activo": true
  }
}
```

**Response 401:** `{ "error": "Credenciales inválidas" }` o similar

---

### POST `/auth/register`
**Acceso:** Público

**Request:**
```json
{
  "nombre": "string",
  "apellido": "string",
  "email": "string",
  "password": "string",
  "telefono": "string",
  "nacionalidad": "string"
}
```

**Response 200:**
```json
{
  "success": true,
  "data": {
    "token": "string (JWT)",
    "usuario": { ... }
  }
}
```

---

### GET `/auth/profile`
**Acceso:** Autenticado (Bearer token)

**Response 200:** objeto `usuario` (misma estructura que login)

---

### GET `/auth/check`
**Acceso:** Autenticado

**Response 200:** `{ "valid": true, "usuario": { ... } }`

---

## Paquetes

### GET `/paquetes/activos`
**Acceso:** Público

**Response 200:** `PaqueteDTO[]`
```json
[{
  "idPaquete": 1,
  "nombrePaquete": "string",
  "descripcion": "string",
  "precioBase": 150.00,
  "duracionDias": 1,
  "estado": "activo | inactivo",
  "lugar": { "idLugar": 1, "nombreLugar": "string", "ciudad": "string" }
}]
```

---

### GET `/paquetes/{id}`
**Acceso:** Público

**Response 200:** `PaqueteDetailDTO` (incluye inventario disponible)

**Response 404:** error

---

### GET `/paquetes/public/activos`
**Acceso:** Público — alias de `/paquetes/activos`

---

### GET `/paquetes/public/{id}`
**Acceso:** Público — alias de `/paquetes/{id}`

---

### GET `/paquetes/public/buscar?nombre=&precioMin=&precioMax=&ciudad=`
**Acceso:** Público  
**Query params:** todos opcionales

**Response 200:** `PaqueteDTO[]`

---

### GET `/paquetes/public/search?nombre=&precioMin=&precioMax=&estado=`
**Acceso:** Público — alias con parámetro `estado`

---

### GET `/paquetes`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `PaqueteDTO[]` (todos, incluyendo inactivos)

---

### POST `/paquetes`
**Acceso:** `ROLE_ADMIN`

**Request:** `PaqueteDTO` (sin id)

**Response 201 / 200:** `PaqueteDTO` creado

---

### PUT `/paquetes/{id}`
**Acceso:** `ROLE_ADMIN`

**Request:** `PaqueteDTO`

**Response 200:** `PaqueteDTO` actualizado

---

### DELETE `/paquetes/{id}`
**Acceso:** `ROLE_ADMIN`

**Response 204 / 200**

---

## Inventario

### GET `/inventario/disponible`
**Acceso:** Público

**Response 200:** `InventarioDTO[]`
```json
[{
  "idInventario": 1,
  "idPaquete": 1,
  "nombrePaquete": "string",
  "fechaSalida": "2026-07-01",
  "fechaRetorno": "2026-07-01",
  "cupoTotal": 20,
  "cupoDisponible": 15
}]
```

---

### GET `/inventario/paquete/{idPaquete}`
**Acceso:** Público

**Response 200:** `InventarioDTO[]` para ese paquete

---

### GET `/inventario/paquete/{idPaquete}/disponible`
**Acceso:** Público

**Response 200:** `InventarioDTO[]` solo con cupo > 0

---

### GET `/inventario/proximas-salidas`
**Acceso:** Público

**Response 200:** `InventarioDTO[]` con fechas futuras y cupo disponible

---

### GET `/inventario`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `InventarioDTO[]` (todos)

---

## Lugares

### GET `/lugares`
**Acceso:** Público

**Response 200:** `LugarDTO[]`
```json
[{ "idLugar": 1, "nombreLugar": "string", "ciudad": "string", "descripcion": "string" }]
```

---

### GET `/lugares/{id}`
**Acceso:** Público

**Response 200:** `LugarDTO`

---

### POST `/lugares`
**Acceso:** `ROLE_ADMIN`

**Request:** `LugarDTO` (sin id)

**Response 201:** `LugarDTO`

---

### PUT `/lugares/{id}`
**Acceso:** `ROLE_ADMIN`

**Request:** `LugarDTO`

**Response 200:** `LugarDTO`

---

### DELETE `/lugares/{id}`
**Acceso:** `ROLE_ADMIN`

**Response 204**

---

## Reservas

### GET `/reservas`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `ReservaDTO[]`
```json
[{
  "idReserva": 1,
  "idUsuario": 2,
  "nombreCliente": "string",
  "idInventario": 1,
  "nombrePaquete": "string",
  "fechaViaje": "2026-07-01",
  "cantidadPersonas": 2,
  "estado": "pendiente | confirmada | cancelada",
  "fechaReserva": "2026-07-10T..."
}]
```

---

### GET `/reservas/mis-reservas`
**Acceso:** Autenticado (token → usuario actual)

**Response 200:** `ReservaDTO[]` del usuario actual

---

### GET `/reservas/my`
**Acceso:** Autenticado — alias de `/reservas/mis-reservas`

---

### GET `/reservas/{id}`
**Acceso:** Autenticado

**Response 200:** `ReservaDTO`

**Response 403/404:** si no pertenece al usuario (o no existe)

---

### POST `/reservas`
**Acceso:** Autenticado

**Request:** `ReservaRequest`
```json
{
  "idInventario": 1,
  "cantidadPersonas": 2
}
```

**Response 200 / 201:** `ReservaDTO` creada

**Response 400:** si no hay cupo disponible

---

### PUT `/reservas/{id}/cancelar`
**Acceso:** Autenticado

**Response 200:** `ReservaDTO` con estado `"cancelada"`

> ⚠️ **BUG ACTUAL:** Esta operación NO restaura el cupo en `inventario_paquetes`. Se debe corregir antes de producción.

---

## Pagos

### GET `/pagos`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `PagoDTO[]`

---

### GET `/pagos/reserva/{reservaId}`
**Acceso:** Autenticado

**Response 200:** `PagoDTO[]` de esa reserva

---

### POST `/pagos`
**Acceso:** Autenticado

**Request:** `PagoRequest`
```json
{ "idReserva": 1, "monto": 300.00, "metodo": "string" }
```

**Response 201:** `PagoDTO`

---

### PUT `/pagos/{id}/procesar`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `PagoDTO` procesado

---

## MercadoPago

### POST `/mercadopago/crear-preferencia`
**Acceso:** Autenticado

**Request:**
```json
{ "reservaId": 1 }
```

**Response 200:**
```json
{ "initPoint": "https://www.mercadopago.com.ar/...", "pagoId": 1 }
```

---

### POST `/mercadopago/webhook`
**Acceso:** Público (sin token)

**Query params:** `?id=<paymentId>&topic=payment` (forma IPN)

**Body:** `{ "data": { "id": "paymentId" } }` (forma Webhooks v2)

**Response:** siempre `200 OK`

> ⚠️ **Sin validación de firma.** Cualquiera puede enviar notificaciones falsas.

---

### GET `/mercadopago/pago-exitoso`
**Acceso:** Público — redirect de MercadoPago tras pago exitoso

---

### GET `/mercadopago/pago-fallido`
**Acceso:** Público — redirect de MercadoPago tras fallo/cancelación

---

## Reportes (Admin)

### GET `/reportes/excel`
**Acceso:** `ROLE_ADMIN`

**Response:** archivo `reservas.xlsx` (Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`)

---

### GET `/reportes/pdf`
**Acceso:** `ROLE_ADMIN`

**Response:** archivo `reservas.pdf` (Content-Type: `application/pdf`)

---

## Usuarios (Admin)

### GET `/usuarios`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `UsuarioDTO[]`

---

### GET `/usuarios/{id}`
**Acceso:** `ROLE_ADMIN`

**Response 200:** `UsuarioDTO`

---

### POST `/usuarios`
**Acceso:** `ROLE_ADMIN`

**Request:** `UsuarioRequest`

**Response 201:** `UsuarioDTO`

---

### DELETE `/usuarios/{id}`
**Acceso:** `ROLE_ADMIN`

**Response 204**

---

## APIs Externas

### GET `/apis-net/dni/{numero}`
**Acceso:** Público

**Response 200:** `ConsultaDniResponseDTO`
```json
{
  "numeroDocumento": "string",
  "nombre": "string",
  "apellidoPaterno": "string",
  "apellidoMaterno": "string"
}
```

---

## WhatsApp

### POST `/whatsapp/enviar?telefono=<numero>`
**Acceso:** Autenticado

**Request:** JSON con mensaje

**Response 200:** confirmación de envío (o error si OpenWA no disponible)

> ⚠️ Usa OpenWA (no oficial). Se reemplazará por Meta WhatsApp Business Cloud API.

---

## Resumen de Endpoints Públicos (sin JWT)

| Endpoint |
|---------|
| POST `/auth/login` |
| POST `/auth/register` |
| GET `/paquetes/activos` |
| GET `/paquetes/{id}` |
| GET `/paquetes/public/**` |
| GET `/inventario/disponible` |
| GET `/inventario/paquete/**` |
| GET `/inventario/proximas-salidas` |
| GET `/lugares` |
| GET `/lugares/{id}` |
| GET `/apis-net/dni/{numero}` |
| POST `/mercadopago/webhook` |
| GET `/mercadopago/pago-exitoso` |
| GET `/mercadopago/pago-fallido` |

**Total endpoints públicos:** 14
