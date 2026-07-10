# Auditoría de Seguridad — Buganvilla Tours

**Fecha:** 2026-07-10  
**Alcance:** Backend, Frontend, Configuración, Integraciones

---

## 1. Resumen de Riesgos por Categoría

| Categoría | Nivel | Estado |
|-----------|-------|--------|
| Secretos en repositorio | CRÍTICO | ❌ Sin corregir |
| Webhook MP sin validación de firma | ALTO | ❌ Sin corregir |
| CORS abierto a `*` | ALTO | ❌ Sin corregir |
| WhatsApp no oficial (OpenWA) | ALTO | ❌ Sin corregir |
| Sin GlobalExceptionHandler (stacktraces expuestos) | ALTO | ❌ Sin corregir |
| JWT — implementación | BAJO | ✅ Bien implementado |
| BCrypt — passwords | BAJO | ✅ Bien implementado |
| CSRF | N/A | ✅ Deshabilitado (API stateless con JWT — correcto) |
| Endpoints admin protegidos | BAJO | ✅ Bien protegidos |
| SQL Injection | BAJO | ✅ Protegido por JPA/Hibernate |
| XSS en frontend | BAJO | ✅ Protegido por React (virtualDOM) |

---

## 2. Hallazgos Detallados

### [CRÍTICO] SEG-01 — Secretos en archivos versionados

**Archivos afectados:**
- `backend/src/main/resources/application.properties`
- `backend/src/main/resources/application-dev.properties`

**Categorías de secretos detectadas (NO se muestran valores):**
- Contraseña de base de datos SQL Server
- Clave secreta JWT (larga, pero hardcodeada)
- Token de acceso a apis.net.pe
- Access token de MercadoPago (permite procesar pagos reales)
- Public key de MercadoPago

**Impacto:** Quien tenga acceso al repositorio (actual o pasado) puede:
- Conectarse directamente a la base de datos
- Generar tokens JWT válidos para cualquier usuario
- Consultar DNI por RENIEC a nombre del proyecto
- Procesar o cancelar pagos reales en MercadoPago

**Corrección:**
1. Mover secretos a variables de entorno del sistema operativo o a un `.env` excluido del repositorio
2. Crear `.env.example` con los nombres de variables y descripción, sin valores
3. Agregar `.env` a `.gitignore`
4. Rotar credenciales comprometidas (especialmente MP access token y JWT secret)
5. Considerar usar Spring Cloud Config o Vault en producción

---

### [ALTO] SEG-02 — Webhook MercadoPago sin validación de firma

**Archivo:** `controller/MercadoPagoController.java`

**Problema:** El endpoint `POST /api/mercadopago/webhook` no valida la firma de la notificación. Un atacante puede enviar una petición POST simulando una notificación de pago aprobado:

```bash
curl -X POST http://localhost:8080/api/mercadopago/webhook?id=12345&topic=payment
```

Si el ID de pago `12345` existe en MercadoPago (incluso de otro comercio), el sistema confirmará la reserva sin que el usuario haya pagado realmente.

**Corrección:** Validar el header `x-signature` usando el `WHATSAPP_APP_SECRET` (o equivalente de MercadoPago). Consultar: https://www.mercadopago.com.ar/developers/es/docs/checkout-pro/additional-content/notifications/webhooks/webhooks

---

### [ALTO] SEG-03 — CORS abierto a todos los orígenes

**Archivo:** `security/SecurityConfig.java`, línea 80

```java
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
configuration.setAllowCredentials(true);
```

**Problema:** Con `allowedOriginPatterns("*")` + `allowCredentials(true)`, cualquier sitio web puede hacer requests autenticados a la API enviando las cookies/credenciales del usuario. Esto abre vectores de ataque CSRF cross-origin.

**Corrección:**
```java
// En application.properties:
app.cors.allowed-origins=http://localhost:3000,http://localhost:4200,https://buganvillatours.com

// En SecurityConfig.java:
@Value("${app.cors.allowed-origins}")
private List<String> allowedOrigins;
configuration.setAllowedOrigins(allowedOrigins);
```

---

### [ALTO] SEG-04 — WhatsApp via OpenWA (integración no oficial)

**Archivo:** `service/OpenWAService.java`

**Problemas:**
1. Viola términos de servicio de Meta — puede resultar en bloqueo del número de teléfono
2. Requiere mantener una sesión de WhatsApp Web activa (QR code) — frágil en producción
3. No hay garantías de entrega ni auditoría
4. La API key de OpenWA puede estar vacía (visto en `application-dev.properties`)

**Corrección:** Reemplazar con WhatsApp Business Cloud API oficial de Meta. Ver Fase 5 del plan de modernización.

---

### [ALTO] SEG-05 — Stacktraces expuestos en respuestas de error

**Causa:** No existe `@RestControllerAdvice` ni `GlobalExceptionHandler`.

**Ejemplo de respuesta actual cuando se produce un error:**
```json
{
  "timestamp": "2026-07-10T...",
  "status": 500,
  "error": "Internal Server Error",
  "trace": "java.lang.RuntimeException: No hay cupo disponible para la cantidad solicitada\n\tat com.buganvilla.buganvillatours.service.impl.ReservaServiceImpl.save(ReservaServiceImpl.java:49)\n\tat..."
}
```

**Impacto:** Revela la estructura interna del código, versiones de librerías, rutas del servidor y detalles que facilitan ataques dirigidos.

**Corrección:** Crear `GlobalExceptionHandler.java` con `@RestControllerAdvice` que mapee excepciones a respuestas limpias sin trace.

---

### [MEDIO] SEG-06 — Validación de inputs débil en endpoints

**Archivos:** Varios controladores (especialmente `ReservaController`, `AuthController`)

**Problema:** Los DTOs de request no tienen anotaciones de validación JSR-380 (`@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Size`, `@Email`). Esto permite:
- Crear reservas con `cantidadPersonas = 0` o negativa
- Registrar usuarios con email vacío o inválido
- Inyectar caracteres especiales en campos de texto

**Corrección:** Agregar anotaciones de validación en los DTOs de request y `@Valid` en los parámetros de los controladores.

---

### [MEDIO] SEG-07 — Token JWT expuesto en logs (potencial)

**Archivo:** `application.properties` + `security/JwtRequestFilter.java`

**Riesgo:** El nivel de log `DEBUG` puede imprimir headers HTTP en logs, incluyendo el header `Authorization: Bearer <token>`. Esto expone tokens JWT en logs de servidor.

**Corrección:** Cambiar `logging.level` a `INFO` en producción. Verificar que el filtro JWT no loguee el token completo.

---

### [BAJO] SEG-08 — Contraseña débil en datos de prueba

**Archivo:** `import.sql`

**Nota:** Los usuarios de seeding tienen password hasheada con BCrypt. La contraseña original parece ser débil (típica de desarrollo). Si estos datos de seeding se aplican en producción, representan un riesgo.

**Corrección:** Documentar que `import.sql` es solo para desarrollo. Usar variables de entorno para las contraseñas de usuarios iniciales en producción.

---

## 3. Aspectos de Seguridad Bien Implementados

### JWT
- Algoritmo: HS256 (adecuado para monolito)
- Clave: 64 caracteres (suficientemente larga)
- Expiración: 24 horas (razonable)
- Validación: verifica firma, expiración, formato
- Manejo de excepciones: MalformedJWT, ExpiredJWT, etc.

### Passwords
- BCryptPasswordEncoder configurado correctamente
- No se almacenan passwords en texto plano
- No se devuelven passwords en respuestas API

### Autorización
- `@EnableMethodSecurity(prePostEnabled = true)` habilitado
- `@PreAuthorize("hasRole('ADMIN')")` en endpoints sensibles
- Rutas bien segregadas entre público, autenticado y admin

### SQL Injection
- Uso de JPA/Hibernate con consultas parametrizadas
- No se construyen queries concatenando strings de usuario

### CSRF
- Deshabilitado correctamente para API stateless con JWT (no usa cookies de sesión)

---

## 4. Checklist de Seguridad para Producción

| Control | Estado | Acción Requerida |
|---------|--------|-----------------|
| Secretos fuera del repositorio | ❌ | Fase 4 — variables de entorno |
| CORS restrictivo | ❌ | Fase 4 — lista de orígenes |
| Firma webhook MP validada | ❌ | Fase 4 — agregar validación |
| WhatsApp oficial | ❌ | Fase 5 — Meta Business API |
| GlobalExceptionHandler | ❌ | Fase 4 — crear handler |
| show-sql=false en prod | ❌ | Fase 4 — mover a perfil dev |
| Validación de inputs | ❌ | Fase 4 — @Valid en controllers |
| Rate limiting en login | ⚠️ Opcional | Considerar en producción |
| HTTPS | N/A | Responsabilidad del proxy/hosting |
| Tokens rotados | ❌ | Rotar tras corregir SEG-01 |
