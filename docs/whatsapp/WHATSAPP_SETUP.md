# WhatsApp Business Cloud API — Configuración

## Estado actual

La integración con WhatsApp está **desactivada por defecto** mediante configuración.
El proveedor activo es `MockWhatsAppProvider`, que solo registra logs informativos.

## Proveedores disponibles

| Proveedor | Propiedad | Comportamiento |
|-----------|-----------|----------------|
| `mock` (default) | `whatsapp.provider=mock` | Solo escribe logs. No envía nada. |
| `meta` | `whatsapp.provider=meta` | Llama a la WhatsApp Business Cloud API (Graph API). |

## Activar el proveedor Meta (producción)

### 1. Crear la App en Meta for Developers

1. Ir a [developers.facebook.com](https://developers.facebook.com)
2. Crear una nueva App del tipo **Business**
3. Agregar el producto **WhatsApp**
4. Obtener un número de teléfono de prueba (sandbox) o un número aprobado

### 2. Crear y aprobar plantillas de mensajes

En el panel de WhatsApp > Plantillas de mensajes:
- `reservation_confirmation` — confirmación de reserva
- `payment_confirmation` — confirmación de pago
- `reservation_cancellation` — cancelación

Ver `PLANTILLAS_WHATSAPP.md` para el contenido exacto.

### 3. Configurar las variables de entorno

Copiar `.env.example` como `.env` y rellenar:

```env
WHATSAPP_ENABLED=true
WHATSAPP_PROVIDER=meta
WHATSAPP_PHONE_NUMBER_ID=<ID del número en Meta>
WHATSAPP_ACCESS_TOKEN=<token de acceso de larga duración>
WHATSAPP_VERIFY_TOKEN=<token aleatorio para verificar webhook>
WHATSAPP_APP_SECRET=<App Secret de la app en Meta>
```

### 4. Configurar el webhook

Ver `WEBHOOKS_WHATSAPP.md` para los pasos detallados.

### 5. Verificar

```bash
# Con los valores configurados, el endpoint /api/whatsapp/webhook debe responder:
curl "https://tu-dominio.com/api/whatsapp/webhook?hub.mode=subscribe&hub.verify_token=TU_TOKEN&hub.challenge=test"
# Respuesta esperada: test
```

## Variables de entorno completas

| Variable | Descripción | Requerida para Meta |
|----------|-------------|---------------------|
| `WHATSAPP_ENABLED` | `true` o `false` | Sí |
| `WHATSAPP_PROVIDER` | `meta` o `mock` | Sí |
| `WHATSAPP_API_BASE_URL` | Base URL de Graph API | No (default: `https://graph.facebook.com`) |
| `WHATSAPP_API_VERSION` | Versión de la API | No (default: `v18.0`) |
| `WHATSAPP_PHONE_NUMBER_ID` | ID del número de WhatsApp Business | Sí |
| `WHATSAPP_BUSINESS_ACCOUNT_ID` | ID de la cuenta business | Para reportes |
| `WHATSAPP_ACCESS_TOKEN` | Token de acceso (larga duración) | Sí |
| `WHATSAPP_VERIFY_TOKEN` | Token para verificar webhook | Para webhooks |
| `WHATSAPP_APP_SECRET` | Para validar firma HMAC de webhooks | Para webhooks |
| `WHATSAPP_DEFAULT_LANGUAGE` | Idioma de las plantillas | No (default: `es_ES`) |
