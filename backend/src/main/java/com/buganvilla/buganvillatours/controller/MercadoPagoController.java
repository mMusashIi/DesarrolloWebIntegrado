package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.model.dto.payment.PreferenceRequestDTO;
import com.buganvilla.buganvillatours.model.dto.payment.PreferenceResponseDTO;
import com.buganvilla.buganvillatours.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Crea una preferencia de MercadoPago para una reserva existente.
     * Devuelve la URL de pago y el ID del pago local creado.
     *
     * POST /api/mercadopago/crear-preferencia
     * Body: { "reservaId": 123 }
     */
    @PostMapping("/crear-preferencia")
    public ResponseEntity<ResponseDTO<PreferenceResponseDTO>> crearPreferencia(
            @RequestBody PreferenceRequestDTO request) {
        try {
            PreferenceResponseDTO response = mercadoPagoService.createPreference(request.getReservaId());
            return ResponseEntity.ok(ResponseDTO.success("Preferencia creada exitosamente", response));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear preferencia MP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Error al crear la preferencia de pago: " + e.getMessage()));
        }
    }

    /**
     * Webhook IPN de MercadoPago. MercadoPago llama a este endpoint cuando
     * el estado de un pago cambia (aprobado, rechazado, pendiente, etc.).
     * Debe ser público (sin autenticación JWT).
     *
     * POST /api/mercadopago/webhook?id=<paymentId>&topic=payment
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestBody(required = false) Map<String, Object> body) {

        log.info("Webhook MP recibido — topic: {}, id: {}, body: {}", topic, id, body);

        try {
            // MercadoPago envía notificaciones con topic=payment
            // El paymentId puede venir como query param 'id' o dentro del body como 'data.id'
            String paymentId = resolvePaymentId(id, topic, body);

            if (paymentId != null) {
                mercadoPagoService.procesarWebhook(paymentId);
            } else {
                log.warn("Webhook recibido sin paymentId válido. topic={}, body={}", topic, body);
            }
        } catch (Exception e) {
            // Siempre retornar 200 a MP para que no reintente el envío infinitamente
            log.error("Error procesando webhook MP: {}", e.getMessage(), e);
        }

        // MercadoPago requiere 200 OK, de lo contrario reintenta el envío
        return ResponseEntity.ok().build();
    }

    /**
     * Redirect de MercadoPago tras un pago exitoso.
     * GET /api/mercadopago/pago-exitoso
     * Redirige al frontend Angular con los parámetros del pago.
     */
    @GetMapping("/pago-exitoso")
    public ResponseEntity<Void> pagoExitoso(
            @RequestParam(value = "collection_id", required = false) String collectionId,
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "payment_id", required = false) String paymentId,
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {

        log.info("Pago exitoso — paymentId: {}, status: {}, reservaId: {}",
                paymentId, collectionStatus, externalReference);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/pago-exitoso")
                .queryParam("payment", "success")
                .queryParamIfPresent("payment_id", java.util.Optional.ofNullable(paymentId))
                .queryParamIfPresent("external_reference", java.util.Optional.ofNullable(externalReference))
                .queryParamIfPresent("collection_status", java.util.Optional.ofNullable(collectionStatus))
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Redirect de MercadoPago tras un pago fallido o cancelado.
     * GET /api/mercadopago/pago-fallido
     * Redirige al frontend Angular con los parámetros del pago.
     */
    @GetMapping("/pago-fallido")
    public ResponseEntity<Void> pagoFallido(
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {

        log.warn("Pago fallido — status: {}, reservaId: {}", collectionStatus, externalReference);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/pago-fallido")
                .queryParam("payment", "failure")
                .queryParamIfPresent("external_reference", java.util.Optional.ofNullable(externalReference))
                .queryParamIfPresent("collection_status", java.util.Optional.ofNullable(collectionStatus))
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Resuelve el paymentId desde distintas fuentes que MercadoPago puede usar:
     * - Query param 'id' cuando topic=payment
     * - Body con { "type": "payment", "data": { "id": "..." } } (formato IPN v2)
     */
    @SuppressWarnings("unchecked")
    private String resolvePaymentId(String id, String topic, Map<String, Object> body) {
        // Formato clásico: ?topic=payment&id=<paymentId>
        if ("payment".equals(topic) && id != null && !id.isBlank()) {
            return id;
        }
        // Formato IPN v2: body = { "type": "payment", "data": { "id": "..." } }
        if (body != null && "payment".equals(body.get("type"))) {
            Object data = body.get("data");
            if (data instanceof Map) {
                Object dataId = ((Map<String, Object>) data).get("id");
                if (dataId != null) {
                    return String.valueOf(dataId);
                }
            }
        }
        return null;
    }
}
