# Auditoría de Base de Datos — Buganvilla Tours

**Fecha:** 2026-07-10  
**Motor:** SQL Server (producción) / H2 (desarrollo)  
**ORM:** Hibernate JPA con `ddl-auto=update`

> El esquema se infirió de las entidades JPA y del archivo `import.sql`. No se ejecutó SQL Server en este análisis.

---

## 1. Esquema de Tablas

### `usuarios`
```sql
id_usuario          BIGINT IDENTITY PRIMARY KEY
nombre              VARCHAR(100) NOT NULL
apellido            VARCHAR(100) NOT NULL
email               VARCHAR(100) NOT NULL UNIQUE
password            VARCHAR(255)
telefono            VARCHAR(30)
nacionalidad        VARCHAR(50)
dni                 VARCHAR(20)
rol                 VARCHAR(20)           -- valores: 'admin', 'cliente'
activo              BIT DEFAULT 1
fecha_creacion      DATETIME DEFAULT CURRENT_TIMESTAMP
fecha_actualizacion DATETIME
```

### `lugares`
```sql
id_lugar        BIGINT IDENTITY PRIMARY KEY
nombre_lugar    VARCHAR(100) NOT NULL
ciudad          VARCHAR(100)
descripcion     VARCHAR(255)
fecha_creacion  DATETIME DEFAULT CURRENT_TIMESTAMP
```

### `paquetes`
```sql
id_paquete      BIGINT IDENTITY PRIMARY KEY
nombre_paquete  VARCHAR(150) NOT NULL
descripcion     NVARCHAR(MAX)
precio_base     DECIMAL(10,2) NOT NULL
duracion_dias   INT
estado          VARCHAR(20)              -- valores: 'activo', 'inactivo'
id_lugar        BIGINT FK → lugares.id_lugar
fecha_creacion  DATETIME DEFAULT CURRENT_TIMESTAMP
```

### `inventario_paquetes`
```sql
id_inventario       BIGINT IDENTITY PRIMARY KEY
id_paquete          BIGINT NOT NULL FK → paquetes.id_paquete
fecha_salida        DATE NOT NULL
fecha_retorno       DATE
cupo_total          INT NOT NULL
cupo_disponible     INT NOT NULL
fecha_creacion      DATETIME DEFAULT CURRENT_TIMESTAMP
```

### `reservas`
```sql
id_reserva          BIGINT IDENTITY PRIMARY KEY
id_usuario          BIGINT NOT NULL FK → usuarios.id_usuario
id_inventario       BIGINT NOT NULL FK → inventario_paquetes.id_inventario
cantidad_personas   INT NOT NULL
fecha_reserva       DATETIME DEFAULT CURRENT_TIMESTAMP
estado              VARCHAR(20)          -- valores: 'pendiente', 'confirmada', 'cancelada'
fecha_creacion      DATETIME DEFAULT CURRENT_TIMESTAMP
```

### `pagos`
```sql
id_pago             BIGINT IDENTITY PRIMARY KEY
id_reserva          BIGINT NOT NULL FK → reservas.id_reserva
monto               DECIMAL(10,2) NOT NULL
metodo              VARCHAR(30)          -- 'MercadoPago', 'transferencia', etc.
estado              VARCHAR(20)          -- 'pendiente', 'completado', 'rechazado', 'en_proceso'
fecha_pago          DATETIME DEFAULT CURRENT_TIMESTAMP
fecha_creacion      DATETIME DEFAULT CURRENT_TIMESTAMP
mp_preference_id    VARCHAR(100)
mp_payment_id       VARCHAR(50)
mp_status           VARCHAR(30)          -- 'approved', 'rejected', 'pending', 'cancelled'
```

---

## 2. Relaciones

```
Lugar (1) ←→ (N) Paquete
Paquete (1) ←→ (N) InventarioPaquete
Usuario (1) ←→ (N) Reserva
InventarioPaquete (1) ←→ (N) Reserva
Reserva (1) ←→ (N) Pago
```

---

## 3. Datos Iniciales (import.sql)

| Tabla | Registros |
|-------|-----------|
| lugares | 4 (Machu Picchu, Vinicunca, Laguna Humantay, Valle Sagrado — todos en Cusco) |
| paquetes | 3 (Full Day Machu Picchu, Trekking Vinicunca, Tour Valle Sagrado VIP) |
| inventario_paquetes | 3 (fechas de salida: 2026-07-01, 07-02, 07-03) |
| usuarios | 3 (1 admin + 2 clientes con passwords BCrypt) |
| reservas | 2 (1 confirmada, 1 pendiente) |
| pagos | 1 (completado, MercadoPago) |

---

## 4. Análisis de Integridad

### 4.1 Claves Primarias
- ✅ Todas las tablas tienen PKs auto-incrementales (`IDENTITY`)
- ✅ Tipos consistentes (`BIGINT` en todas)

### 4.2 Claves Foráneas
- ✅ `paquetes.id_lugar` → `lugares.id_lugar` (ON DELETE: depende de config JPA)
- ✅ `inventario_paquetes.id_paquete` → `paquetes.id_paquete`
- ✅ `reservas.id_usuario` → `usuarios.id_usuario`
- ✅ `reservas.id_inventario` → `inventario_paquetes.id_inventario`
- ✅ `pagos.id_reserva` → `reservas.id_reserva`

**⚠️ Riesgo:** Con `ddl-auto=update`, Hibernate puede no crear FK constraints correctamente en SQL Server. Se recomienda verificar que las restricciones existen realmente en la base de datos.

### 4.3 Restricciones de Unicidad
- ✅ `usuarios.email` — UNIQUE (evita registros duplicados)
- ⚠️ No hay restricción UNIQUE en `inventario_paquetes(id_paquete, fecha_salida)` — podría haber registros de inventario duplicados para el mismo paquete y fecha

### 4.4 Valores Nulos
- `usuarios.password` — nullable en DDL (pero no debería serlo para usuarios normales)
- `pagos.mp_preference_id`, `mp_payment_id`, `mp_status` — nullable (correcto para pagos no-MP)
- `inventario_paquetes.fecha_retorno` — nullable (correcto para tours de 1 día)

### 4.5 Estados Permitidos

| Tabla | Campo | Valores |
|-------|-------|---------|
| `usuarios` | `rol` | `'admin'`, `'cliente'` |
| `paquetes` | `estado` | `'activo'`, `'inactivo'` |
| `reservas` | `estado` | `'pendiente'`, `'confirmada'`, `'cancelada'` |
| `pagos` | `estado` | `'pendiente'`, `'completado'`, `'rechazado'`, `'en_proceso'` |
| `pagos` | `mp_status` | `'approved'`, `'rejected'`, `'pending'`, `'cancelled'`, `'in_process'` |

**⚠️ Sin restricciones CHECK:** No hay constraints CHECK en la BD que fuercen estos valores. La lógica está solo en la capa de servicio Java. Un INSERT directo podría poner un estado inválido.

---

## 5. Análisis de Concurrencia

### 5.1 Riesgo de Cupos Negativos

**Escenario de race condition:**
1. Dos usuarios (A y B) intentan reservar el último cupo simultáneamente
2. `ReservaServiceImpl.save()` es `@Transactional` pero **sin lock sobre `inventario_paquetes`**
3. Ambas transacciones leen `cupo_disponible = 1`
4. Ambas pasan la verificación de disponibilidad
5. Ambas ejecutan `reducirCupo()` → `cupo_disponible = -1`

**Resultado:** `cupo_disponible` puede quedar negativo. La BD no tiene restricción CHECK `cupo_disponible >= 0`.

**Solución A (optimista):** Agregar `@Version` en `InventarioPaquete`. La segunda transacción falla con `OptimisticLockException`.

**Solución B (pesimista):** Usar `@Lock(LockModeType.PESSIMISTIC_WRITE)` en la query de inventario dentro de la transacción de reserva.

**Solución C (BD):** Agregar `CHECK (cupo_disponible >= 0)` como constraint a nivel de base de datos como segunda línea de defensa.

### 5.2 Riesgo de Cupos No Restaurados al Cancelar

**Bug detectado:** `ReservaServiceImpl.cancelarReserva()` cambia el estado de la reserva a "cancelada" pero NO llama a `inventarioPaqueteService.aumentarCupo()`.

**Impacto:** Cada cancelación reduce permanentemente los cupos disponibles. Con el tiempo, el sistema reporta cupos = 0 aunque haya plazas disponibles.

**Contraste:** `deleteById()` SÍ restaura cupos. El bug existe solo en `cancelarReserva()`.

---

## 6. Análisis de Rendimiento Potencial

### 6.1 Consultas de Inventario
Las consultas de inventario disponible se ejecutan frecuentemente (página pública). Con muchos registros históricos, puede volverse lenta si no hay índices en `fecha_salida`.

**Recomendación:** Agregar índice en `inventario_paquetes(fecha_salida, cupo_disponible)`.

### 6.2 N+1 en Reportes
`ReservaRepository.findAll()` carga todas las reservas y luego Hibernate ejecuta N queries para cada `reserva.getUsuario()` y `reserva.getInventario().getPaquete()`.

**Para 100 reservas:** ~200 queries adicionales.

**Solución:** `JOIN FETCH` en la query del reporte.

### 6.3 Tipo NVARCHAR(MAX) en descripción de paquetes
`paquetes.descripcion` es `NVARCHAR(MAX)`. Para listados masivos, esto puede impactar el rendimiento.

**Solución:** Usar proyecciones o DTOs que excluyan la descripción larga en listados.

---

## 7. Hallazgos Clasificados

| ID | Descripción | Severidad | Corrección |
|----|-------------|-----------|-----------|
| DB-01 | cancelarReserva() no restaura cupo | CRÍTICO | Agregar llamada a aumentarCupo() |
| DB-02 | Race condition: cupos negativos posibles | ALTO | @Lock o @Version en InventarioPaquete |
| DB-03 | Sin restricción CHECK en cupo_disponible | ALTO | ALTER TABLE + constraint |
| DB-04 | FK constraints no verificadas (ddl=update) | MEDIO | Verificar constraints en SQL Server real |
| DB-05 | Sin UNIQUE en (id_paquete, fecha_salida) | MEDIO | Agregar índice UNIQUE |
| DB-06 | Estados sin CHECK constraints en BD | MEDIO | Agregar CHECK constraints por tabla |
| DB-07 | N+1 en reportes | MEDIO | JOIN FETCH en query |
| DB-08 | Sin índice en fecha_salida de inventario | BAJO | CREATE INDEX después de medir |
| DB-09 | usuarios.password nullable en DDL | BAJO | NOT NULL constraint |
| DB-10 | show-sql=true en producción | BAJO | Mover a perfil dev |

---

## 8. Migraciones Propuestas (Seguras e Idempotentes)

Estas migraciones se documentan como propuesta. NO se ejecutan en esta fase de diagnóstico.

```sql
-- DB-01: Ningún cambio de SQL, es fix en código Java

-- DB-02 / DB-03: Restricción CHECK para evitar cupos negativos
ALTER TABLE inventario_paquetes 
ADD CONSTRAINT CHK_cupo_disponible_no_negativo CHECK (cupo_disponible >= 0);

-- DB-05: Restricción UNIQUE para evitar duplicados de inventario
ALTER TABLE inventario_paquetes 
ADD CONSTRAINT UQ_inventario_paquete_fecha UNIQUE (id_paquete, fecha_salida);

-- DB-06: CHECK constraints para estados
ALTER TABLE reservas 
ADD CONSTRAINT CHK_reserva_estado CHECK (estado IN ('pendiente', 'confirmada', 'cancelada'));

ALTER TABLE pagos 
ADD CONSTRAINT CHK_pago_estado CHECK (estado IN ('pendiente', 'completado', 'rechazado', 'en_proceso'));

-- DB-08: Índice en fecha_salida (solo si se confirma necesidad por métricas)
CREATE INDEX IDX_inventario_fecha_salida ON inventario_paquetes(fecha_salida);
```

**Estrategia de rollback:** Cada ALTER es reversible con DROP CONSTRAINT / DROP INDEX.
