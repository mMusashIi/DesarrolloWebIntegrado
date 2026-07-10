# Plantillas de WhatsApp Business

Las plantillas deben crearse y aprobarse en Meta Business Manager antes de usarlas.

## reservation_confirmation

**Nombre:** `reservation_confirmation`  
**Idioma:** `es_ES`  
**Categoría:** `UTILITY`

**Cuerpo:**
```
¡Hola {{1}}! Tu reserva para el paquete *{{2}}* ha sido registrada.
📅 Fecha de salida: {{3}}
👥 Personas: {{4}}
Reserva #{{5}}

¡Gracias por elegir Buganvilla Tours!
```

**Parámetros (en orden):**
1. Nombre del cliente
2. Nombre del paquete
3. Fecha de salida (dd/MM/yyyy)
4. Cantidad de personas
5. ID de reserva

---

## payment_confirmation

**Nombre:** `payment_confirmation`  
**Idioma:** `es_ES`  
**Categoría:** `UTILITY`

**Cuerpo:**
```
¡Hola {{1}}! Hemos recibido tu pago de S/ {{2}}.
Tu reserva #{{3}} ha sido *CONFIRMADA*. 🎉

¡Nos vemos pronto en el tour!
Buganvilla Tours 🌺
```

**Parámetros:**
1. Nombre del cliente
2. Monto pagado
3. ID de reserva

---

## reservation_cancellation

**Nombre:** `reservation_cancellation`  
**Idioma:** `es_ES`  
**Categoría:** `UTILITY`

**Cuerpo:**
```
Hola {{1}}, tu reserva para el paquete *{{2}}* ha sido cancelada.

Si tienes preguntas, contáctanos. ¡Esperamos verte pronto!
Buganvilla Tours 🌺
```

**Parámetros:**
1. Nombre del cliente
2. Nombre del paquete

---

## Notas de implementación

- Las plantillas solo pueden enviarse a números que hayan optado por recibirlas (opt-in).
- En entorno de prueba (sandbox de Meta), solo se pueden enviar mensajes a números registrados en el sandbox.
- El tiempo de aprobación de plantillas suele ser 24-72 horas.
- La implementación real de envío de plantillas está marcada como `TODO` en `MetaWhatsAppCloudProvider.java`.
