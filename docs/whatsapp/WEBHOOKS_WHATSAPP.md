# Webhooks de WhatsApp Business Cloud API

## Endpoints implementados

| Método | Ruta | Propósito |
|--------|------|-----------|
| `GET` | `/api/whatsapp/webhook` | Verificación del webhook (challenge de Meta) |
| `POST` | `/api/whatsapp/webhook` | Recepción de eventos entrantes |

> **Estado actual:** Skeleton implementado. La validación de firma HMAC-SHA256 está diseñada pero pendiente de implementación completa en `MetaWhatsAppCloudProvider`.

## Flujo de verificación (GET)

Meta envía:
```
GET /api/whatsapp/webhook
  ?hub.mode=subscribe
  &hub.verify_token=TU_VERIFY_TOKEN
  &hub.challenge=CHALLENGE_ALEATORIO
```

El endpoint debe responder con el valor de `hub.challenge` si `hub.verify_token` coincide con `WHATSAPP_VERIFY_TOKEN`.

## Flujo de eventos (POST)

Meta envía notificaciones cuando:
- Un usuario envía un mensaje al número de negocio
- Un mensaje cambia de estado (enviado, entregado, leído)

### Validación de firma

Cada request POST incluye el header `X-Hub-Signature-256`:
```
X-Hub-Signature-256: sha256=<HMAC-SHA256 del body con WHATSAPP_APP_SECRET>
```

El backend debe verificar esta firma antes de procesar cualquier evento.

```java
// Algoritmo de validación:
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(appSecret.getBytes(), "HmacSHA256"));
byte[] hash = mac.doFinal(rawBody.getBytes());
String expectedSignature = "sha256=" + Hex.encodeHexString(hash);
// Comparar con header usando MessageDigest.isEqual() para timing-safe comparison
```

## Idempotencia

Para evitar procesar el mismo evento dos veces (Meta puede reenviar eventos):
- Tabla `whatsapp_events` con columna `message_id` unique
- Al recibir un evento, insertar el `message_id` — si ya existe, ignorar

## Logs

Los logs del webhook NO deben incluir:
- Contenido del mensaje
- Número de teléfono completo (solo últimos 4 dígitos)
- Datos personales del remitente

Solo se loguea: tipo de evento, hash del teléfono, timestamp.

## Configurar el webhook en Meta

1. En el panel de Meta for Developers → tu App → WhatsApp → Configuration
2. Callback URL: `https://tu-dominio.com/api/whatsapp/webhook`
3. Verify Token: el valor de `WHATSAPP_VERIFY_TOKEN`
4. Suscribirse a: `messages`
5. Clic en "Verify and Save"
