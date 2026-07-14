# Bases de datos por microservicio

## Principio de aislamiento

Cada servicio es el único dueño de su base de datos. Ningún servicio accede directamente a la BD de otro. El acceso cruzado se hace solo a través de la API REST del servicio propietario.

## BuganvillaAuth — auth-service

**Entidades:** Usuario

```sql
CREATE TABLE Usuarios (
    id_usuario    BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    telefono      VARCHAR(20),
    rol           VARCHAR(20) NOT NULL DEFAULT 'cliente',
    fecha_creacion DATETIME2 DEFAULT GETDATE()
);
```

Datos de prueba iniciales cargados por `DataSeeder`:
- Admin: `admin@buganvilla.com` / `admin123`
- Cliente: `cliente@buganvilla.com` / `cliente123`

## BuganvillaCatalogo — catalogo-service

**Entidades:** Lugar, Paquete

```sql
CREATE TABLE Lugares (
    id_lugar     BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(500),
    pais         VARCHAR(50),
    ciudad       VARCHAR(100)
);

CREATE TABLE Paquetes (
    id_paquete     BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre_paquete VARCHAR(200) NOT NULL,
    descripcion    TEXT,
    precio_base    DECIMAL(10,2) NOT NULL,
    duracion_dias  INT NOT NULL,
    activo         BIT DEFAULT 1,
    id_lugar       BIGINT REFERENCES Lugares(id_lugar),
    imagen_url     VARCHAR(500),
    fecha_creacion DATETIME2 DEFAULT GETDATE()
);
```

## BuganvillaInventario — inventario-service

**Entidades:** InventarioPaquete

> **Importante**: La columna `id_paquete` es un FK lógico — no tiene constraint de FK real porque Paquete vive en otra BD.

```sql
CREATE TABLE InventarioPaquetes (
    id_inventario     BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_paquete        BIGINT NOT NULL,  -- FK lógico a BuganvillaCatalogo.Paquetes
    fecha_salida      DATE NOT NULL,
    fecha_retorno     DATE NOT NULL,
    cupo_total        INT NOT NULL,
    cupo_disponible   INT NOT NULL,
    activo            BIT DEFAULT 1,
    fecha_creacion    DATETIME2 DEFAULT GETDATE()
);
```

Endpoints internos (requieren `X-Internal-Token`):
- `GET /api/inventario/{id}/verificar?cantidad=N`
- `PUT /api/inventario/{id}/reducir-cupo?cantidad=N`
- `PUT /api/inventario/{id}/aumentar-cupo?cantidad=N`

## BuganvillaReservas — reserva-service

**Entidades:** Reserva

> `id_usuario` e `id_inventario` son FK lógicos — no tienen constraint de FK real porque los propietarios viven en otras BDs.

```sql
CREATE TABLE Reservas (
    id_reserva        BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_usuario        BIGINT NOT NULL,    -- FK lógico a BuganvillaAuth.Usuarios
    id_inventario     BIGINT NOT NULL,    -- FK lógico a BuganvillaInventario.InventarioPaquetes
    cantidad_personas INT NOT NULL,
    fecha_reserva     DATETIME2 DEFAULT GETDATE(),
    estado            VARCHAR(20) DEFAULT 'pendiente',  -- pendiente|confirmada|cancelada
    fecha_creacion    DATETIME2 DEFAULT GETDATE()
);
```

## BuganvillaPagos — pago-service

**Entidades:** Pago

> `id_reserva` es FK lógico a BuganvillaReservas.

```sql
CREATE TABLE Pagos (
    id_pago              BIGINT IDENTITY(1,1) PRIMARY KEY,
    id_reserva           BIGINT NOT NULL,   -- FK lógico a BuganvillaReservas.Reservas
    monto                DECIMAL(10,2) NOT NULL,
    metodo               VARCHAR(30),        -- mercadopago|efectivo|transferencia
    estado               VARCHAR(20) DEFAULT 'pendiente',  -- pendiente|completado|rechazado|en_proceso
    fecha_pago           DATETIME2 DEFAULT GETDATE(),
    fecha_creacion       DATETIME2 DEFAULT GETDATE(),
    mp_preference_id     VARCHAR(100),
    mp_payment_id        VARCHAR(50),
    mp_status            VARCHAR(30)
);
```

## Servicios sin base de datos

### notificacion-service (:8086)
Sin JPA. Lee configuración de WhatsApp y APIs Net Pe desde `application.properties`. No persiste datos.

### reporte-service (:8087)
Sin JPA. Obtiene datos leyendo de reserva-service y pago-service via REST. Genera archivos en memoria (Excel/PDF) y los envía directamente en la respuesta HTTP.

## Notas sobre integridad referencial

En una arquitectura de microservicios, las FK lógicas (sin constraint de BD) son una decisión consciente. La integridad se mantiene a nivel de aplicación:

- Si un usuario se elimina de `BuganvillaAuth`, sus reservas en `BuganvillaReservas` quedan huérfanas (sin constraint). En producción se puede manejar con un evento de dominio o una llamada REST que cancele las reservas activas.
- Si un inventario se modifica en `BuganvillaInventario`, los datos de `idInventario` en `BuganvillaReservas` siguen siendo válidos como identificador, pero datos derivados (nombre del paquete, fecha de salida) requieren llamar a inventario-service.
