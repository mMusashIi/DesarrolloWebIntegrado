# Seguridad — WhatsApp Business Cloud API

## Principios aplicados

### 1. Desactivado por defecto
```properties
whatsapp.enabled=false
whatsapp.provider=mock
```
La integración real no se activa sin configuración explícita.

### 2. Credenciales en variables de entorno
Nunca en el repositorio. Ver `.env.example` para los nombres de variables.

### 3. Logs sin datos sensibles
- Teléfonos: solo últimos 4 dígitos (`***1234`)
- Contenido de mensajes: no se loguea
- Access tokens: nunca en logs

### 4. Validación de firma de webhook
Todos los eventos POST de Meta deben validarse con HMAC-SHA256 usando `WHATSAPP_APP_SECRET`.
La comparación debe ser timing-safe (`MessageDigest.isEqual()`), no `String.equals()`.

### 5. Procesamiento asíncrono
Las notificaciones se envían con `@Async` para no bloquear el flujo principal.
Los errores se capturan y loguan como warnings — no abortan la transacción principal.

### 6. Sin librerías no oficiales
No usar OpenWA, Baileys, Selenium, QR persistente ni scraping de WhatsApp Web.
Solo `WhatsApp Business Cloud API` (Graph API de Meta).

## Riesgos y mitigaciones

| Riesgo | Mitigación |
|--------|-----------|
| Token de acceso filtrado | Variable de entorno, rotación periódica, acceso mínimo necesario |
| Webhook spoofing | Validación HMAC-SHA256 con App Secret |
| Procesamiento duplicado | Tabla `whatsapp_events` con `message_id` unique |
| Rate limiting de Meta | Procesamiento asíncrono con cola interna si se supera el límite |
| Mensajes a números sin opt-in | Validar opt-in antes de enviar (requerido por política de Meta) |

## Política de Meta — Opt-in

Los usuarios deben haber dado su consentimiento explícito para recibir mensajes de WhatsApp.
En Buganvilla Tours, el opt-in se obtiene en el formulario de registro:
- Checkbox: "Acepto recibir confirmaciones y notificaciones por WhatsApp"
- El estado de opt-in se almacena en `usuarios.whatsapp_opt_in` (pendiente de implementar)

## Referencias

- [Documentación oficial Meta WhatsApp](https://developers.facebook.com/docs/whatsapp)
- [Verificación de webhooks](https://developers.facebook.com/docs/graph-api/webhooks/getting-started)
- [Validación de firma](https://developers.facebook.com/docs/messenger-platform/webhooks#validate-payloads)
