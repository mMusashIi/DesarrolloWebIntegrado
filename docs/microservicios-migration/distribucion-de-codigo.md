# Distribucion de codigo - Monolito a microservicios

Origen principal: `backend/src/main/java/com/buganvilla/buganvillatours/`.

La carpeta `backend/` queda como referencia del monolito original. El stack
vigente usa las carpetas de servicios y el `api-gateway`.

## Resumen por servicio

| Servicio | Codigo principal | Cambios clave |
|---|---|---|
| `api-gateway` | Nuevo | Enruta `/api/**`, valida JWT compartido y usa URLs por variables `*_SERVICE_URL`. |
| `auth-service` | Usuarios, auth, JWT, seeder | Mantiene la base real de usuarios y emite JWT con `sub`, `idUsuario` y `rol`. |
| `catalogo-service` | Paquetes, lugares | Conserva entidades de catalogo; operaciones de escritura requieren `ROLE_ADMIN`. |
| `inventario-service` | InventarioPaquete | Reemplaza relacion directa con paquete por `idPaquete`; agrega endpoints internos de cupo. |
| `reserva-service` | Reservas | Reemplaza relaciones JPA por `idUsuario` e `idInventario`; consume inventario y notificacion por REST. |
| `pago-service` | Pagos, MercadoPago | Reemplaza relacion JPA con reserva por `idReserva`; consume reserva y notificacion por REST. |
| `notificacion-service` | WhatsApp, DNI/RUC | Expone endpoints internos de notificacion y endpoints publicos DNI/RUC. |
| `reporte-service` | Reportes Excel/PDF | Genera reportes consultando reserva y pago por REST. |

## Seguridad

- Solo `auth-service` consulta la base de usuarios.
- `auth-service` firma tokens con `JWT_SECRET`.
- Los demas servicios validan la firma y reconstruyen el principal desde los
  claims `idUsuario` y `rol`.
- Los servicios consumidores no tienen `CustomUserDetailsService` local ni
  proveedor DAO: son JWT-only.
- Las llamadas internas mutables usan `X-Internal-Token`.

## auth-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `model/entity/Usuario` | `model/entity/Usuario` | Conservado |
| `model/dto/AuthDTO`, `UsuarioDTO`, `UsuarioRequest`, `ResponseDTO` | `model/dto/` | Conservados |
| `repository/UsuarioRepository` | `repository/UsuarioRepository` | Conservado |
| `service/UsuarioService`, `service/impl/UsuarioServiceImpl` | `service/` | Conservados |
| `security/*` | `security/*` | Conservado; JWT ahora incluye `idUsuario` y `rol` |
| `controller/AuthController`, `controller/UsuarioController` | `controller/` | Conservados |
| `config/DataSeeder` | `config/DataSeeder` | Solo seeds de usuarios |

## catalogo-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `model/entity/Paquete`, `Lugar` | `model/entity/` | Conservados |
| `model/dto/PaqueteDTO`, `PaqueteDetailDTO`, `LugarDTO`, `ResponseDTO` | `model/dto/` | Conservados |
| `repository/PaqueteRepository`, `LugarRepository` | `repository/` | Conservados |
| `service/PaqueteService`, `LugarService` y sus impl | `service/` | Conservados |
| `controller/PaqueteController`, `LugarController` | `controller/` | Conservados |
| `security/Jwt*`, `SecurityConfig` | `security/` | JWT-only; sin consulta local de usuarios |

## inventario-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `model/entity/InventarioPaquete` | `model/entity/InventarioPaquete` | `Paquete` se reemplaza por `Long idPaquete` |
| `model/dto/InventarioDTO`, `ResponseDTO` | `model/dto/` | Conservados |
| `repository/InventarioPaqueteRepository` | `repository/` | Queries usan `idPaquete` |
| `service/InventarioPaqueteService` y su impl | `service/` | Conservados |
| `controller/InventarioController` | `controller/` | Agrega `verificar`, `reducir-cupo`, `aumentar-cupo` internos |
| `security/Jwt*`, `SecurityConfig` | `security/` | JWT-only; endpoints internos por `X-Internal-Token` |

## reserva-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `model/entity/Reserva` | `model/entity/Reserva` | `Usuario` e `InventarioPaquete` se reemplazan por IDs |
| `model/dto/ReservaDTO`, `ReservaRequest`, `ResponseDTO` | `model/dto/` | DTOs propios del servicio |
| `repository/ReservaRepository` | `repository/` | Queries por `idUsuario` e `idInventario` |
| `service/ReservaService` y su impl | `service/` | Usa `InventarioClient` y `NotificacionClient` |
| `controller/ReservaController` | `controller/` | Protege consultas por usuario y endpoints admin/internos |
| `client/InventarioClient`, `client/NotificacionClient` | Nuevo | REST interno con `X-Internal-Token` |
| `security/Jwt*`, `SecurityConfig` | `security/` | JWT-only; confirmar/cancelar aceptan admin o token interno |

## pago-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `model/entity/Pago` | `model/entity/Pago` | `Reserva` se reemplaza por `Long idReserva` |
| `model/dto/PagoDTO`, `PagoRequest`, `ResponseDTO` | `model/dto/` | DTOs propios del servicio |
| `model/dto/payment/PreferenceRequestDTO` | `model/dto/payment/` | Agrega `monto`, `descripcion`, `cantidad` |
| `repository/PagoRepository` | `repository/` | Queries por `idReserva` |
| `service/MercadoPagoService` | `service/` | Usa `ReservaClient` y `NotificacionClient` |
| `controller/PagoController`, `MercadoPagoController` | `controller/` | Conservados con DTO extendido |
| `client/ReservaClient`, `client/NotificacionClient` | Nuevo | REST interno con `X-Internal-Token` |
| `security/Jwt*`, `SecurityConfig` | `security/` | JWT-only |

## notificacion-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `whatsapp/*` | `whatsapp/` | Conservado |
| `service/ApisNetPeService` | `service/` | Conservado |
| `controller/WhatsAppController`, `ApisNetPeController` | `controller/` | Conservados |
| `controller/NotificacionController` | Nuevo | Endpoints internos de reserva, pago y cancelacion |
| `model/dto/Notificacion*Request` | Nuevo | DTOs de comunicacion interna |
| `security/Jwt*`, `SecurityConfig` | `security/` | JWT-only; `/api/notificacion/**` requiere `X-Internal-Token` |

## reporte-service

| Origen monolito | Destino | Estado |
|---|---|---|
| `service/ReporteService` | `service/ReporteService` | Consulta reserva y pago por REST |
| `controller/ReporteController` | `controller/ReporteController` | Propaga JWT a clientes internos |
| `client/ReservaClient`, `client/PagoClient` | Nuevo | REST hacia reserva y pago |
| `model/dto/ReservaDTO`, `PagoDTO`, `ResponseDTO` | Nuevo | Copias locales de contratos necesarios |
| `security/Jwt*`, `SecurityConfig` | `security/` | JWT-only |
