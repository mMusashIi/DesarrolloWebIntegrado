# Auditoría Backend — Buganvilla Tours

**Fecha:** 2026-07-10  
**Tecnología:** Spring Boot 3.4.11 / Java 21 / JPA / SQL Server

---

## 1. Estructura de Paquetes

```
com.buganvilla.buganvillatours/
├── BuganvillatoursApplication.java
├── config/
│   ├── DataSeeder.java              Datos iniciales (lugares, paquetes, usuarios)
│   ├── MercadoPagoAppConfig.java    Configura token MP en @PostConstruct
│   └── RestClientConfig.java        Bean de RestTemplate
├── controller/                      11 controladores REST
├── model/
│   ├── dto/                         11 DTOs + 2 sub-paquetes (api/, payment/, whatsapp/)
│   ├── entity/                      6 entidades JPA
│   └── mapper/                      6 mappers MapStruct
├── repository/                      6 interfaces JPA Repository
├── security/                        JWT + Spring Security
├── service/                         8 interfaces + 7 implementaciones
└── util/
    └── SecurityUtil.java            Obtiene el usuario actual del SecurityContext
```

**Total de clases:** ~70

---

## 2. Controladores y Endpoints (Resumen)

Ver `CONTRATO_API_ACTUAL.md` para el detalle completo.

| Controlador | Endpoints | Roles |
|------------|-----------|-------|
| `AuthController` | login, register, profile, check | Público / Autenticado |
| `PaqueteController` | CRUD + búsqueda | Público + Admin |
| `InventarioController` | Read + admin | Público + Admin |
| `LugarController` | CRUD | Público + Admin |
| `ReservaController` | CRUD + cancelar + confirmar | Autenticado + Admin |
| `PagoController` | CRUD + procesar | Autenticado + Admin |
| `MercadoPagoController` | preferencia + webhook + redirects | Autenticado + Público |
| `ReporteController` | excel + pdf | Admin |
| `UsuarioController` | CRUD | Admin |
| `ApisNetPeController` | consulta DNI | Público |
| `WhatsAppController` | enviar mensaje | Autenticado |

---

## 3. Entidades JPA

### `Usuario`
- `@Table(name="usuarios")`
- Campos: `idUsuario`, `nombre`, `apellido`, `email` (UNIQUE), `password` (BCrypt), `telefono`, `nacionalidad`, `dni`, `rol`, `activo`, `fechaCreacion`, `fechaActualizacion`
- Relaciones: `@OneToMany` con `Reserva`

### `Lugar`
- `@Table(name="lugares")`
- Campos: `idLugar`, `nombreLugar`, `ciudad`, `descripcion`, `fechaCreacion`
- Relaciones: `@OneToMany` con `Paquete`

### `Paquete`
- `@Table(name="paquetes")`
- Campos: `idPaquete`, `nombrePaquete`, `descripcion`, `precioBase`, `duracionDias`, `estado`, `idLugar` (FK), `fechaCreacion`
- Relaciones: `@ManyToOne` con `Lugar`, `@OneToMany` con `InventarioPaquete`

### `InventarioPaquete`
- `@Table(name="inventario_paquetes")`
- Campos: `idInventario`, `idPaquete` (FK), `fechaSalida`, `fechaRetorno`, `cupoTotal`, `cupoDisponible`, `fechaCreacion`
- Relaciones: `@ManyToOne` con `Paquete`, `@OneToMany` con `Reserva`
- ⚠️ Sin `@Version` para control optimista de concurrencia

### `Reserva`
- `@Table(name="reservas")`
- Campos: `idReserva`, `idUsuario` (FK), `idInventario` (FK), `cantidadPersonas`, `fechaReserva`, `estado`
- Métodos de negocio: `cancelar()` (cambia estado pero NO restaura cupo), `confirmar()`
- Relaciones: `@ManyToOne` con `Usuario` e `InventarioPaquete`, `@OneToMany` con `Pago`

### `Pago`
- `@Table(name="pagos")`
- Campos: `idPago`, `idReserva` (FK), `monto`, `metodo`, `estado`, `fechaPago`, `fechaCreacion`, `mpPreferenceId`, `mpPaymentId`, `mpStatus`
- Métodos de negocio: `procesarPago()`, `rechazarPago()`

---

## 4. Hallazgos por Severidad

### BLOQUEANTE

Ninguno (el sistema compila y arranca).

---

### CRÍTICO

#### C1 — Bug: cancelarReserva() no restaura cupo
**Archivo:** `service/impl/ReservaServiceImpl.java`, método `cancelarReserva()` (línea 153)

**Comportamiento actual:**
```java
public Reserva cancelarReserva(Long id) {
    return reservaRepository.findById(id)
        .map(reserva -> {
            reserva.cancelar();  // ← solo cambia estado a "cancelada"
            return reservaRepository.save(reserva);
        })
        // ...
}
```

**Falta esta llamada:**
```java
inventarioPaqueteService.aumentarCupo(reserva.getInventario().getIdInventario(), reserva.getCantidadPersonas());
```

**Riesgo:** Cupos se pierden permanentemente al cancelar una reserva. Con el tiempo, el sistema dice "sin cupo" aunque el tour tenga plazas disponibles.

**Contraste:** `deleteById()` SÍ llama `aumentarCupo()` correctamente (línea 113).

**Solución:** Agregar llamada a `aumentarCupo()` dentro de `cancelarReserva()`.

**Rollback:** No aplica (la corrección es aditiva).

---

#### C2 — Secretos hardcodeados en archivos versionados
**Archivos:** `application.properties`, `application-dev.properties`

**Categorías de secretos presentes:**
- Contraseña de SQL Server
- JWT secret key
- Token de apis.net.pe
- Access token de MercadoPago
- Public key de MercadoPago

**Riesgo:** Cualquier persona con acceso al repositorio (o que lo clonó en el pasado) tiene estas credenciales. Los tokens de MercadoPago permiten procesar pagos reales.

**Solución:** Mover todos los secretos a variables de entorno. Crear `.env.example` con nombres y descripciones pero sin valores. Nunca versionarlos.

---

### ALTO

#### A1 — Race condition en creación de reservas
**Archivo:** `service/impl/ReservaServiceImpl.java`, método `save()` (línea 40)

**Problema:** El flujo `verificarDisponibilidad()` → `reducirCupo()` no está protegido contra concurrencia. Si dos requests simultáneos pasan la verificación al mismo tiempo, ambos reducirán el cupo aunque solo haya espacio para uno.

**Escenario de fallo:**
```
T0: cupoDisponible = 1
T1a: Request A → verificarDisponibilidad(1) → true
T1b: Request B → verificarDisponibilidad(1) → true (aún es 1)
T2a: Request A → reducirCupo() → cupoDisponible = 0
T2b: Request B → reducirCupo() → cupoDisponible = -1  ← PROBLEMA
```

**Solución:** Agregar `@Lock(LockModeType.PESSIMISTIC_WRITE)` en la consulta de `InventarioPaquete`, o agregar `@Version` en la entidad para control optimista.

---

#### A2 — No existe GlobalExceptionHandler
**Archivo:** No existe `GlobalExceptionHandler.java` ni ningún `@RestControllerAdvice`

**Problema:** Las excepciones no controladas (como `RuntimeException("No hay cupo disponible")`) resultan en respuestas HTTP 500 con stacktrace completo — filtrando información interna al cliente.

**Respuesta actual ante error:**
```json
{
  "timestamp": "...",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/reservas",
  "trace": "java.lang.RuntimeException: No hay cupo disponible..."  // ← INFORMACIÓN INTERNA EXPUESTA
}
```

**Solución:** Crear `@RestControllerAdvice GlobalExceptionHandler` que mapee excepciones conocidas a códigos HTTP apropiados (400, 404, 409, etc.) con mensajes sin stacktrace.

---

#### A3 — WhatsApp via OpenWA (integración no oficial)
**Archivo:** `service/OpenWAService.java`

**Problema:** OpenWA automatiza WhatsApp Web mediante un cliente no oficial. Viola los términos de servicio de Meta, puede ser bloqueado en cualquier momento sin aviso, y requiere un número de teléfono activo con sesión QR.

**Solución:** Reemplazar con WhatsApp Business Cloud API oficial de Meta (Fase 5 del plan).

---

#### A4 — Webhook MercadoPago sin validación de firma
**Archivo:** `controller/MercadoPagoController.java`, método `webhook()`

**Problema:** El endpoint recibe notificaciones de MercadoPago sin validar la firma. Un atacante podría enviar notificaciones falsas de "pago aprobado" para confirmar reservas sin pagar realmente.

**Solución:** Validar el header `x-signature` de MercadoPago antes de procesar el webhook.

---

#### A5 — Atomicidad incompleta en procesamiento de pago
**Archivo:** `service/MercadoPagoService.java`

**Problema:** Cuando se aprueba un pago, se llama `pago.procesarPago()` y luego `reserva.confirmar()` en operaciones separadas. Si la segunda operación falla, el pago queda marcado como "completado" pero la reserva permanece "pendiente".

**Solución:** Envolver ambas operaciones en un método `@Transactional` único.

---

### MEDIO

#### M1 — CORS abierto a todos los orígenes
**Archivo:** `security/SecurityConfig.java`, línea 80

```java
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
```

**Riesgo:** Cualquier sitio web puede hacer requests autenticados a la API (con credentials).

**Solución:** Reemplazar `*` por lista configurable de orígenes permitidos via `application.properties`.

---

#### M2 — Tests de integración sin perfil H2
**Carpeta:** `src/test/`

**Problema:** Los tests de integración usan `@SpringBootTest` sin especificar `@ActiveProfiles("dev")`, por lo que intentan conectar a SQL Server. En un entorno sin SQL Server activo, todos fallan con `ApplicationContext failure`.

**Resultado baseline:** 130 tests ejecutados, 44 errores, BUILD FAILURE.

**Solución:** Agregar `@ActiveProfiles("dev")` o `@TestPropertySource` en los tests de integración para usar H2.

---

#### M3 — show-sql=true en producción
**Archivo:** `application.properties`, línea 9

```properties
spring.jpa.show-sql=true
```

**Riesgo:** Todas las queries SQL aparecen en logs, incluyendo datos potencialmente sensibles. En producción esto puede llenar discos rápidamente.

**Solución:** Mover `show-sql=true` solo al perfil `dev`.

---

#### M4 — Validación de inputs incompleta
**Archivos:** Varios controladores

**Problema:** Algunos endpoints no usan `@Valid` en sus parámetros `@RequestBody`. Por ejemplo, `POST /reservas` no valida que `cantidadPersonas > 0`.

**Solución:** Agregar anotaciones `@Valid` + `@NotNull`, `@Min(1)`, etc. en los DTOs de request.

---

#### M5 — Métodos sin implementar en servicios
**Archivos:**
- `service/impl/UsuarioServiceImpl.java` — método `update()` no implementado (lanza excepción o no hace nada)
- `service/impl/PaqueteServiceImpl.java` — método `activarPaquete()` no implementado

**Riesgo:** Si se llaman desde el frontend, fallarán silenciosamente.

---

### BAJO

#### B1 — N+1 queries en reportes
**Archivo:** `service/impl/ReportServiceImpl.java`

**Problema:** `reservaRepository.findAll()` carga las reservas con lazy loading. Al acceder a `reserva.getUsuario().getNombre()` e `reserva.getInventario().getPaquete().getNombrePaquete()`, Hibernate ejecuta N queries adicionales.

**Solución:** Agregar `@Query("SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.inventario i JOIN FETCH i.paquete")` en el repositorio.

---

#### B2 — Código duplicado / Servicios duplicados
**Archivos:** Se detectaron dos interfaces similares: `ReporteService.java` y `ReportService.java` (una en español, una en inglés). Pueden ser redundantes.

---

#### B3 — Logging potencialmente verboso
**Archivo:** `application.properties`

`logging.level.com.buganvilla.buganvillatours=DEBUG` en producción genera logs muy verbosos.

**Solución:** Cambiar a `INFO` en producción.

---

## 5. Fortalezas del Backend

- Arquitectura bien separada (controller → service → repository)
- JWT correctamente implementado (HS256, 24h expiration, validación completa)
- BCrypt para passwords
- MapStruct para mapeo entity ↔ DTO (evita serialización de entidades)
- Métodos de creación y actualización de reservas correctamente `@Transactional`
- Método `deleteById()` restaura cupos correctamente
- MercadoPago integrado completamente (preferencias + webhooks + redirects)
- Generación de Excel y PDF funcional
- Consulta RENIEC (DNI) integrada
- DataSeeder con datos de prueba realistas
- `SecurityUtil.java` para obtener usuario actual de forma centralizada
