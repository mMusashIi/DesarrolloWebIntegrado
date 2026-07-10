# Arquitectura Actual — Buganvilla Tours

**Fecha:** 2026-07-10

---

## 1. Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                     USUARIO FINAL                               │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP (port 3000 dev)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   FRONTEND — React 19 + Vite 7                  │
│                                                                  │
│  Páginas: Home | Packages | Reservations | Admin | About | ...  │
│  Auth: AuthContext (JWT en localStorage)                        │
│  Proxy: /api → http://localhost:8080 (Vite devServer)           │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP REST /api/** (JWT Bearer token)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   BACKEND — Spring Boot 3.4.11                  │
│                                                                  │
│  SecurityConfig → JwtRequestFilter → Controllers               │
│  Controllers → Services → Repositories → JPA                   │
│                                                                  │
│  Módulos: Auth | Paquetes | Inventario | Reservas | Pagos       │
│           Lugares | Reportes | Usuarios | WhatsApp | DNI        │
└──────┬──────────────┬─────────────────┬────────────────────────┘
       │              │                  │
       ▼              ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐
│  SQL Server  │  │  MercadoPago │  │  Integraciones Externas   │
│  :1433       │  │  API v1      │  │                          │
│  (producción)│  │  (pagos)     │  │  • apis.net.pe (DNI)     │
│              │  └──────────────┘  │  • OpenWA (WhatsApp) ⚠️  │
│  H2 :mem     │                    └──────────────────────────┘
│  (desarrollo)│
└──────────────┘

Callbacks:
MercadoPago → POST /api/mercadopago/webhook (sin validar firma ⚠️)
```

---

## 2. Capas del Backend

```
HTTP Request
    ↓
[JwtRequestFilter] — extrae y valida JWT, establece SecurityContext
    ↓
[SecurityConfig] — autoriza según rol y ruta
    ↓
[@RestController] — valida Request, llama al servicio
    ↓
[@Service] — lógica de negocio, transacciones
    ↓
[@Repository] — acceso a datos JPA
    ↓
[Hibernate/JPA] — SQL generado automáticamente
    ↓
[SQL Server / H2]
```

---

## 3. Flujo de Autenticación

```
1. POST /api/auth/login
   Request: { email, password }
       ↓
   CustomUserDetailsService.loadUserByUsername(email)
       ↓
   BCryptPasswordEncoder.matches(password, hash)
       ↓
   JwtTokenUtil.generateToken(userDetails)
       ↓
   Response: { token, usuario }

2. Request autenticada
   Header: Authorization: Bearer <token>
       ↓
   JwtRequestFilter.doFilterInternal()
       → parseJwt() extrae token del header
       → JwtTokenUtil.validateToken(token)
       → SecurityContextHolder.setAuthentication(auth)
       ↓
   Spring Security authorizeHttpRequests evalúa roles
```

---

## 4. Flujo de Reserva con Pago

```
Cliente
  → POST /api/reservas { idInventario, cantidadPersonas }
       → verificarDisponibilidad() ← SIN LOCK ⚠️
       → reducirCupo()
       → reservaRepository.save() — estado: "pendiente"
  ← { reserva creada }
  
  → POST /api/mercadopago/crear-preferencia { reservaId }
       → PreferenceClient.create() (llamada a MP API)
       → Crea Pago local estado: "pendiente"
  ← { initPoint: "https://www.mercadopago.com.ar/..." }

Cliente → [MercadoPago Checkout] → Paga

MercadoPago → POST /api/mercadopago/webhook?id=<paymentId>
       → PaymentClient.get(paymentId)
       → si approved: reserva.confirmar() + pago.procesarPago()
       → OpenWAService.enviarMensaje() ← EN THREAD SEPARADO ⚠️
       → Response: 200 OK siempre

Cliente → GET /api/mercadopago/pago-exitoso (redirect)
```

---

## 5. Modelo de Datos (Relaciones)

```
Lugar ──┐
        │ 1:N
        ▼
     Paquete ──┐
               │ 1:N
               ▼
          InventarioPaquete ──┐
          (fecha, cupos)      │ 1:N
                              ▼
Usuario ──────────────────► Reserva ──┐
  1:N                                 │ 1:N
                                      ▼
                                    Pago
                                    (MercadoPago info)
```

---

## 6. Configuración de Seguridad

### Endpoints Públicos (sin JWT)
- `/api/auth/login`, `/api/auth/register`
- `/api/paquetes/activos`, `/api/paquetes/{id}`, `/api/paquetes/public/**`
- `/api/inventario/disponible`, `/api/inventario/paquete/**`, `/api/inventario/proximas-salidas`
- `/api/lugares`, `/api/lugares/{id}`
- `/api/apis-net/**`
- `/api/mercadopago/webhook`, `/api/mercadopago/pago-exitoso`, `/api/mercadopago/pago-fallido`

### Endpoints con Autenticación (cualquier rol)
- `/api/auth/profile`, `/api/auth/check`
- `POST /api/reservas`
- `/api/reservas/mis-reservas`, `/api/reservas/my`, `GET /api/reservas/{id}`
- `PUT /api/reservas/{id}/cancelar`
- `/api/pagos/reserva/{id}`
- `POST /api/pagos`
- `POST /api/mercadopago/crear-preferencia`
- `POST /api/whatsapp/enviar`

### Endpoints Solo Admin (`ROLE_ADMIN`)
- `POST/PUT/DELETE /api/paquetes`
- `GET /api/inventario`
- `POST/PUT/DELETE /api/lugares`
- `GET /api/reservas`
- `PUT /api/pagos/{id}/procesar`, `GET /api/pagos`
- `GET /api/reportes/excel`, `GET /api/reportes/pdf`
- `/api/usuarios/**`

---

## 7. Infraestructura Actual

| Componente | Estado | Nota |
|-----------|--------|------|
| Frontend (React) | Local solamente | Puerto 3000 con Vite |
| Backend (Spring Boot) | Local solamente | Puerto 8080 |
| Base de datos | SQL Server local (producción) | Puerto 1433, BD: BuganvillaTours1 |
| Base de datos | H2 en memoria (desarrollo) | Solo con `--spring.profiles.active=dev` |
| Docker | **NO EXISTE** | Debe crearse en Fase 6 |
| NGINX | **NO EXISTE** | Debe crearse para SPA en Docker |
| CI/CD | **NO EXISTE** | Fuera del alcance actual |
| HTTPS/TLS | **NO EXISTE** | Responsabilidad del hosting |

---

## 8. Decisiones de Arquitectura Observadas

| Decisión | Justificación inferida | Valoración |
|---------|----------------------|-----------|
| Monorepo (front + back juntos) | Simplicidad de desarrollo | ✅ Adecuado para equipo pequeño |
| JWT stateless | Sin sesiones en servidor | ✅ Correcto para REST API |
| MapStruct para DTOs | Evita exponer entidades directamente | ✅ Buena práctica |
| H2 en dev | Sin necesidad de SQL Server para desarrollo | ✅ Práctico |
| OpenWA para WhatsApp | Sin acceso a Meta Business API | ❌ No oficial, debe reemplazarse |
| `ddl-auto=update` | Auto-sincronización del esquema | ⚠️ Solo aceptable en desarrollo |
| Secrets en properties | Simplicidad inicial | ❌ Riesgo de seguridad crítico |
